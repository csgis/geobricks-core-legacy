package de.csgis.geobricks.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.csgis.geobricks.CustomConfigurator;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

/**
 * Reads the plugins configuration from <code>gbapp-conf.json</code> and loads
 * all the required plugins from the classpath. It sets:
 * 
 * <ul>
 * <li>{@link Geobricks#ATTR_PLUGINS_DESC} ({@link PluginDescriptor}[]): Plugin
 * descriptors.
 * <li>{@link Geobricks#ATTR_PLUGINS_CONF} ({@link PluginDescriptor}[]): Plugin
 * configurations for application. Content as read from
 * <code>gbapp-conf.json</code> without any modification.</li>
 * <li>{@link Geobricks#ATTR_CONFIGURATORS} ({@link CustomConfigurator}[]):
 * Custom application configurators.</li>
 * </ul>
 * 
 * @author vicgonco
 * 
 */
@Singleton
public class PluginListener implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(PluginListener.class);

	public static final String MODULES_PATH = "webapp/modules";
	public static final String APP_CONF_PATH = "/WEB-INF/conf/gbapp-conf.json";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			initialize(sce.getServletContext());
		} catch (Exception e) {
			// The application will not be shown so we need to tell at least
			// the developer
			logger.error("The application could not be loaded", e);
		}
	}

	public void initialize(ServletContext context) throws IOException {
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		Set<PluginDescriptor> descriptors = new HashSet<PluginDescriptor>();

		InputStream stream = context.getResourceAsStream(APP_CONF_PATH);
		String json = IOUtils.toString(stream);
		stream.close();

		JSONObject appConf = JSONObject.fromObject(json);

		for (Object key : appConf.keySet()) {
			URL pluginConfUrl = getClass().getResource(
					"/conf/" + key + "-pluginconf.json");
			PluginDescriptor pluginDescriptor = getModulesAndStyles(context,
					pluginConfUrl);

			if (pluginDescriptor != null) {
				JSONObject pluginConf = JSONObject.fromObject(IOUtils
						.toString(pluginConfUrl.openStream()));
				processPluginConf(pluginConf, pluginDescriptor, configurators);
				descriptors.add(pluginDescriptor);
			}
		}

		context.setAttribute(Geobricks.ATTR_PLUGINS_CONF, appConf);
		context.setAttribute(Geobricks.ATTR_PLUGINS_DESC,
				descriptors.toArray(new PluginDescriptor[descriptors.size()]));
		context.setAttribute(Geobricks.ATTR_CONFIGURATORS, configurators
				.toArray(new CustomConfigurator[configurators.size()]));
	}

	/**
	 * Obtains the plugin descriptor with the modules and styles configured from
	 * the given plugin descriptor file URL. It scans the classpath (jar or
	 * directory) for modules and styles.
	 * 
	 * @param context
	 *            The servlet context.
	 * @param pluginConf
	 *            The URL of the plugin configuration file.
	 * @return The plugin descriptor with <b>only</b> the modules and
	 *         stylesheets configured.
	 * @throws IOException
	 *             If any I/O error occurs while obtaining the modules and
	 *             stylesheets.
	 */
	public PluginDescriptor getModulesAndStyles(ServletContext context,
			URL pluginConf) throws IOException {
		String protocol = pluginConf.getProtocol();

		if (protocol.equals("jar")) {
			try {
				String path = new URL(pluginConf.getFile()).getPath();
				logger.debug("Getting path: " + path);
				ZipInputStream jar = new ZipInputStream(new FileInputStream(
						path.substring(0, path.indexOf('!'))));
				return getModulesAndStylesFromJar(jar);
			} catch (MalformedURLException e) {
				throw new IOException(e);
			}
		} else if (protocol.equals("file")) {
			File confDir = new File(pluginConf.getPath()).getParentFile();
			File root = confDir.getParentFile();
			return getModulesAndStylesFromDir(root);
		} else {
			logger.error("Unknown protocol '" + protocol
					+ "' for plugin descriptor: " + pluginConf.toString()
					+ ". Ignoring");
			return null;
		}
	}

	/**
	 * Obtains the plugin descriptor with the modules and styles configured from
	 * the given jar file.
	 * 
	 * @param jar
	 *            The jar file to scan.
	 * @return The plugin descriptor with <b>only</b> the modules and
	 *         stylesheets configured.
	 * @throws IOException
	 *             If any I/O error occurs while reading the jar file.
	 */
	public PluginDescriptor getModulesAndStylesFromJar(ZipInputStream jar)
			throws IOException {
		PluginDescriptor descriptor = new PluginDescriptor();

		ZipEntry entry;
		while ((entry = jar.getNextEntry()) != null) {
			String name = entry.getName();
			if (name.matches(MODULES_PATH + File.separator + ".+")) {
				processEntry(name, descriptor);
			}
		}

		return descriptor;
	}

	/**
	 * Obtains the plugin descriptor with the modules and styles configured from
	 * the given directory.
	 * 
	 * @param root
	 *            The directory to scan.
	 * @return The plugin descriptor with <b>only</b> the modules and
	 *         stylesheets configured.
	 */
	public PluginDescriptor getModulesAndStylesFromDir(File root) {
		PluginDescriptor descriptor = new PluginDescriptor();

		String path = "webapp" + File.separator + "modules";
		File[] moduleFiles = new File(root, path).listFiles();
		for (File file : moduleFiles) {
			processEntry(path + File.separator + file.getName(), descriptor);
		}
		return descriptor;
	}

	/**
	 * Processes the given entry.
	 * 
	 * If it's a <code>.js</code> file within the {@link #MODULES_PATH}
	 * directory it will be added to the modules set of the plugin descriptor.
	 * 
	 * If it's a <code>.css</code> file within the {@link #MODULES_PATH}
	 * directory it will be added to the styles list of the plugin descriptor.
	 * 
	 * @param entry
	 *            The entry to process.
	 * @param descriptor
	 *            The descriptor where the module or style should be added.
	 */
	public void processEntry(String entry, PluginDescriptor descriptor) {
		int length = PluginListener.MODULES_PATH.length();

		if (entry.startsWith(PluginListener.MODULES_PATH)
				&& entry.endsWith(".css")) {
			String style = "modules/" + entry.substring(length + 1);
			descriptor.getStyles().add(style);
		} else if (entry.startsWith(PluginListener.MODULES_PATH)
				&& entry.endsWith(".js")) {
			String module = entry.substring(length + 1, entry.length() - 3);
			descriptor.getModules().add(module);
		}
	}

	/**
	 * Updates the plugin descriptor and the set of custom configurators with
	 * the provided plugin configuration.
	 * 
	 * @param conf
	 *            The plugin configuration
	 * @param descriptor
	 *            The plugin descriptor to update.
	 * @param configurators
	 *            The set of custom configurators.
	 */
	@SuppressWarnings("unchecked")
	public void processPluginConf(JSONObject conf, PluginDescriptor descriptor,
			Set<CustomConfigurator> configurators) {
		if (!conf.has("id")) {
			return;
		}

		descriptor.setId(conf.getString("id"));

		JSONObject defaultConf = conf.getJSONObject("default-conf");
		if (defaultConf != null && !defaultConf.isNullObject()) {
			descriptor.setDefaultConfiguration(defaultConf);
		}

		JSONObject deps = conf.getJSONObject("non-require-deps");
		if (deps != null && !deps.isNullObject()) {
			for (Object key : deps.keySet()) {
				String name = key.toString();
				descriptor.getDependencies().put(name, deps.getString(name));
			}
		}

		if (conf.has("custom-configurator")) {
			String configurator = conf.getString("custom-configurator");
			try {
				Class<?> c = Class.forName(configurator);
				if (CustomConfigurator.class.isAssignableFrom(c)) {
					configurators.add((CustomConfigurator) c.newInstance());
				}
			} catch (ClassNotFoundException e) {
				logger.error("Cannot instantiate custom configurator. "
						+ "Class not found: " + configurator + ". Ignoring.", e);
			} catch (IllegalAccessException | InstantiationException e) {
				logger.error("Cannot instantiate custom configurator: "
						+ configurator + ". Ignoring.", e);
			}
		}

		if (conf.has("css")) {
			List<String> styles = descriptor.getStyles();
			styles.clear();

			JSONArray array = conf.getJSONArray("css");
			styles.addAll(JSONArray.toCollection(array));
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing
	}
}
