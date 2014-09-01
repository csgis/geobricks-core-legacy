package de.csgis.geobricks.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.google.inject.Injector;

import de.csgis.geobricks.CustomConfigurator;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

/**
 * Reads the plugin description for all plugins and sets:
 * 
 * <ul>
 * <li>{@link Geobricks#ATTR_PLUGINS_DESC} ({@link PluginDescriptor}[]): Plugin
 * descriptors.
 * <li>{@link Geobricks#ATTR_CONFIGURATORS} ({@link CustomConfigurator}[]):
 * Custom application configurators.</li>
 * </ul>
 * .
 * 
 * It uses an injected instance of {@link PluginScanner} to obtain all available
 * plugin descriptors.
 * 
 * @author vicgonco
 * 
 */
@Singleton
public class PluginListener implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(PluginListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		initialize(sce.getServletContext());
	}

	public void initialize(ServletContext context) {
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		Set<PluginDescriptor> descriptors = new HashSet<PluginDescriptor>();

		Injector injector = (Injector) context.getAttribute(Injector.class
				.getCanonicalName());
		PluginScanner scanner = injector.getInstance(PluginScanner.class);

		scanner.init(context);
		while (scanner.next()) {
			PluginDescriptor descriptor = new PluginDescriptor();

			processEntries(scanner.entries(), descriptor);
			processPluginConf(scanner.pluginConf(), descriptor, configurators);

			descriptors.add(descriptor);
		}

		context.setAttribute(Geobricks.ATTR_PLUGINS_DESC,
				descriptors.toArray(new PluginDescriptor[descriptors.size()]));
		context.setAttribute(Geobricks.ATTR_CONFIGURATORS, configurators
				.toArray(new CustomConfigurator[configurators.size()]));
	}

	/**
	 * Adds the provided modules and styles to the plugin descriptor.
	 * 
	 * @param entries
	 *            The module and style entries to process. Only <i>.css</i> and
	 *            <i>.js</i> entries are handled.
	 * @param descriptor
	 *            The descriptor where the modules and styles must be added.
	 */
	public void processEntries(Set<String> entries, PluginDescriptor descriptor) {
		int length = PluginScanner.MODULES_PATH.length();

		for (String entry : entries) {
			if (entry.startsWith(PluginScanner.MODULES_PATH)
					&& entry.endsWith(".css")) {
				String style = "modules/" + entry.substring(length + 1);
				descriptor.getStyles().add(style);
			} else if (entry.startsWith(PluginScanner.MODULES_PATH)
					&& entry.endsWith(".js")) {
				String module = entry.substring(length + 1, entry.length() - 3);
				descriptor.getModules().add(module);
			}
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
