package de.csgis.geobricks.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

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
 * </ul>
 * 
 * @author vicgonco
 * 
 */
@Singleton
public class PluginListener implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(PluginListener.class);

	public static final String MODULES_PATH = "webapp/modules";
	public static final String STYLES_PATH = "webapp/styles";
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
		Set<PluginDescriptor> descriptors = new HashSet<PluginDescriptor>();

		InputStream stream = context.getResourceAsStream(APP_CONF_PATH);
		String json = IOUtils.toString(stream);
		stream.close();

		JSONObject appConf = JSONObject.fromObject(json);

		for (Object key : appConf.keySet()) {
			URL pluginConfUrl = getClass().getResource(
					"/conf/" + key + "-conf.json");
			PluginDescriptor pluginDescriptor = getModulesAndStyles(context,
					pluginConfUrl);
			pluginDescriptor.setId(key.toString());
			if (pluginDescriptor != null) {
				JSONObject pluginConf = JSONObject.fromObject(IOUtils
						.toString(pluginConfUrl.openStream()));
				processPluginConf(pluginConf, pluginDescriptor);
				descriptors.add(pluginDescriptor);
			}
		}

		context.setAttribute(Geobricks.ATTR_PLUGINS_CONF, appConf);
		context.setAttribute(Geobricks.ATTR_PLUGINS_DESC,
				descriptors.toArray(new PluginDescriptor[descriptors.size()]));
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
				processJSEntry(name, descriptor);
				processCSSEntry(name, descriptor);
			} else if (name.matches(STYLES_PATH + File.separator + ".+")) {
				processCSSEntry(name, descriptor);
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

		File[] moduleFiles = new File(root, MODULES_PATH).listFiles();
		for (File file : moduleFiles) {
			String entry = MODULES_PATH + File.separator + file.getName();
			processJSEntry(entry, descriptor);
			processCSSEntry(entry, descriptor);
		}

		File[] styleFiles = new File(root, STYLES_PATH).listFiles();
		for (File file : styleFiles) {
			String entry = STYLES_PATH + File.separator + file.getName();
			processCSSEntry(entry, descriptor);
		}

		return descriptor;
	}

	/**
	 * Processes the given entry.
	 * 
	 * If it's a <code>.css</code> file within the {@link #MODULES_PATH} or the
	 * {@link #STYLES_PATH} directory it will be added to the styles list of the
	 * plugin descriptor.
	 * 
	 * @param entry
	 *            The entry to process.
	 * @param descriptor
	 *            The descriptor where the module or style should be added.
	 */
	public void processCSSEntry(String entry, PluginDescriptor descriptor) {
		if (entry.endsWith(".css")) {
			int modulesLength = PluginListener.MODULES_PATH.length();
			int stylesLength = PluginListener.STYLES_PATH.length();

			if (entry.startsWith(PluginListener.MODULES_PATH)) {
				String style = "modules/" + entry.substring(modulesLength + 1);
				descriptor.getStyles().add(style);
			} else if (entry.startsWith(PluginListener.STYLES_PATH)) {
				String style = "styles/" + entry.substring(stylesLength + 1);
				descriptor.getStyles().add(style);
			}
		}
	}

	/**
	 * Processes the given entry.
	 * 
	 * If it's a <code>.js</code> file within the {@link #MODULES_PATH}
	 * directory it will be added to the modules set of the plugin descriptor.
	 * 
	 * @param entry
	 *            The entry to process.
	 * @param descriptor
	 *            The descriptor where the module or style should be added.
	 */
	public void processJSEntry(String entry, PluginDescriptor descriptor) {
		if (entry.endsWith(".js")
				&& entry.startsWith(PluginListener.MODULES_PATH)) {
			int modulesLength = PluginListener.MODULES_PATH.length();
			String module = entry.substring(modulesLength + 1,
					entry.length() - 3);
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
	public void processPluginConf(JSONObject conf, PluginDescriptor descriptor) {
		JSONObject defaultConf = conf.getJSONObject("default-conf");
		if (defaultConf != null && !defaultConf.isNullObject()) {
			descriptor.setDefaultConfiguration(defaultConf);
		}

		JSONObject requirejs = conf.getJSONObject("requirejs");
		if (requirejs != null && !requirejs.isNullObject()) {
			JSONObject paths = requirejs.getJSONObject("paths");
			if (paths != null && !paths.isNullObject()) {
				descriptor.setRequirePaths(paths);
			}

			JSONObject shim = requirejs.getJSONObject("shim");
			if (shim != null && !shim.isNullObject()) {
				descriptor.setRequireShim(shim);
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing
	}
}
