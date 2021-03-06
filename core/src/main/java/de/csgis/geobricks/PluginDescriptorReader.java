package de.csgis.geobricks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Loads all the required plugins from the classpath.
 * 
 * @author vicgonco
 */
public class PluginDescriptorReader {
	private static final Logger logger = Logger
			.getLogger(PluginDescriptorReader.class);

	public static final Path WEBAPP = new Path("webapp");
	public static final String MODULES_PATH = WEBAPP.modules().path();
	public static final String STYLES_PATH = WEBAPP.styles().path();
	public static final String THEME_PATH = WEBAPP.theme().path();

	private Map<String, PluginDescriptor> descriptors = new HashMap<String, PluginDescriptor>();

	/**
	 * Returns the descriptors for the given plugins in the same order.
	 * 
	 * @param plugins
	 *            The plugin identifiers to obtain the descriptors.
	 * @return The descriptors for required plugins.
	 * @throws IOException
	 *             if any I/O error occurs while reading the plugin descriptors.
	 */
	public PluginDescriptor[] getDescriptors(List<String> plugins)
			throws IOException {
		List<PluginDescriptor> list = new ArrayList<PluginDescriptor>();

		for (Object key : plugins) {
			if (!this.descriptors.containsKey(key)) {
				URL pluginConfUrl = getClass().getResource(
						"/conf/" + key.toString() + "-conf.json");
				if (pluginConfUrl == null) {
					logger.error("Cannot load plugin: " + key);
					continue;
				}

				JSONObject pluginConf = JSONObject.fromObject(IOUtils
						.toString(pluginConfUrl));
				PluginDescriptor descriptor = new PluginDescriptor(
						key.toString());

				PluginDescriptor[] dependencies = getDescriptors(getPluginDependencies(
						key.toString(), pluginConf));
				Collections.addAll(descriptor.getDependencies(), dependencies);

				processModulesAndStyles(pluginConfUrl, descriptor);
				processPluginConf(pluginConf, descriptor);

				this.descriptors.put(key.toString(), descriptor);
			}

			list.add(this.descriptors.get(key));
		}

		return list.toArray(new PluginDescriptor[list.size()]);
	}

	/**
	 * Obtains the dependencies for the specified plugin.
	 * 
	 * @param id
	 *            The identifier of the plugin.
	 * @param conf
	 *            The plugin configuration, where dependencies are defined.
	 * @return The list of dependencies for the given plugin. If the plugin has
	 *         no dependencies it returns an empty list. It never returns
	 *         <code>null</code>.
	 */
	List<String> getPluginDependencies(String id, JSONObject conf) {
		List<String> deps = new ArrayList<String>();

		try {
			JSONArray array = conf.getJSONArray("deps");
			for (int i = 0; i < array.size(); i++) {
				deps.add(array.getString(i));
			}
		} catch (JSONException e) {
			logger.error("Invalid dependencies ('deps') for plugin " + id
					+ ". Expecting array of strings. Got: " + conf.get("deps"));
		}

		return deps;
	}

	/**
	 * Obtains the plugin descriptor with the modules and styles configured from
	 * the given plugin descriptor file URL. It scans the classpath (jar or
	 * directory) for modules and styles.
	 * 
	 * @param pluginConf
	 *            The URL of the plugin configuration file.
	 * @param descriptor
	 *            The descriptor where the modules and/or styles should be
	 *            added.
	 * @throws IOException
	 *             If any I/O error occurs while obtaining the modules and
	 *             stylesheets.
	 */
	void processModulesAndStyles(URL pluginConf, PluginDescriptor descriptor)
			throws IOException {
		String protocol = pluginConf.getProtocol();

		if (protocol.equals("jar")) {
			try {
				String path = new URL(pluginConf.getFile()).getPath();
				logger.debug("Getting path: " + path);
				String jarPath = path.substring(0, path.indexOf('!'));
				processModulesAndStylesFromJar(jarPath, descriptor);
			} catch (MalformedURLException e) {
				throw new IOException(e);
			}
		} else if (protocol.equals("file")) {
			File confDir = new File(pluginConf.getPath()).getParentFile();
			File root = confDir.getParentFile();
			processModulesAndStylesFromDir(root, descriptor);
		} else {
			logger.error("Unknown protocol '" + protocol
					+ "' for plugin descriptor: " + pluginConf.toString()
					+ ". Ignoring");
		}
	}

	/**
	 * Obtains the plugin descriptor with the modules and styles configured from
	 * the given jar file.
	 * 
	 * @param zipFile
	 *            The path of the jar file to scan.
	 * @param descriptor
	 *            The descriptor where the modules and/or styles should be
	 *            added.
	 * @throws IOException
	 *             If any I/O error occurs while reading the jar file.
	 */
	void processModulesAndStylesFromJar(String zipFile,
			PluginDescriptor descriptor) throws IOException {
		ZipEntry entry;

		ZipInputStream jar = new ZipInputStream(new FileInputStream(zipFile));
		while ((entry = jar.getNextEntry()) != null) {
			String name = entry.getName();
			if (name.matches(MODULES_PATH + File.separator + ".+")) {
				processJSEntry(name, descriptor);
				processCSSEntry(name, descriptor);
			}
		}
		jar.close();

		jar = new ZipInputStream(new FileInputStream(zipFile));
		while ((entry = jar.getNextEntry()) != null) {
			String name = entry.getName();
			if (name.matches(STYLES_PATH + File.separator + ".+")) {
				processCSSEntry(name, descriptor);
			}
		}
		jar.close();

		jar = new ZipInputStream(new FileInputStream(zipFile));
		while ((entry = jar.getNextEntry()) != null) {
			String name = entry.getName();
			if (name.matches(THEME_PATH + File.separator + ".+")) {
				processCSSEntry(name, descriptor);
			}
		}
		jar.close();
	}

	/**
	 * Obtains the plugin descriptor with the modules and styles configured from
	 * the given directory.
	 * 
	 * @param root
	 *            The directory to scan.
	 * @param descriptor
	 *            The descriptor where the modules and/or styles should be
	 *            added.
	 */
	void processModulesAndStylesFromDir(File root, PluginDescriptor descriptor) {
		File modulesDir = new File(root, MODULES_PATH);
		for (File file : FileUtils.listFiles(modulesDir, null, true)) {
			String relativePath = file.getAbsolutePath().substring(
					modulesDir.getAbsolutePath().length());
			String entry = (MODULES_PATH + relativePath)
					.replaceAll("\\\\", "/");
			processJSEntry(entry, descriptor);
			processCSSEntry(entry, descriptor);
		}

		processCSSFiles(new File(root, STYLES_PATH).listFiles(), STYLES_PATH,
				descriptor);
		processCSSFiles(new File(root, THEME_PATH).listFiles(), THEME_PATH,
				descriptor);
	}

	private void processCSSFiles(File[] files, String dir,
			PluginDescriptor descriptor) {
		if (files != null) {
			for (File file : files) {
				String entry = dir + File.separator + file.getName();
				processCSSEntry(entry, descriptor);
			}
		}
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
	void processCSSEntry(String entry, PluginDescriptor descriptor) {
		if (entry.endsWith(".css")) {
			int modulesLength = PluginDescriptorReader.MODULES_PATH.length();
			int stylesLength = PluginDescriptorReader.STYLES_PATH.length();
			int themeLength = PluginDescriptorReader.THEME_PATH.length();

			if (entry.startsWith(PluginDescriptorReader.MODULES_PATH)) {
				String style = "modules/" + entry.substring(modulesLength + 1);
				descriptor.getStyles().add(style);
			} else if (entry.startsWith(PluginDescriptorReader.STYLES_PATH)) {
				String style = "styles/" + entry.substring(stylesLength + 1);
				descriptor.getStyles().add(style);
			} else if (entry.startsWith(PluginDescriptorReader.THEME_PATH)) {
				String style = "theme/" + entry.substring(themeLength + 1);
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
	void processJSEntry(String entry, PluginDescriptor descriptor) {
		if (entry.endsWith(".js")
				&& entry.startsWith(PluginDescriptorReader.MODULES_PATH)) {
			int modulesLength = PluginDescriptorReader.MODULES_PATH.length();
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
	void processPluginConf(JSONObject conf, PluginDescriptor descriptor) {
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
}
