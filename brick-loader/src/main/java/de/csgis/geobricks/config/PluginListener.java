package de.csgis.geobricks.config;

import java.util.HashSet;
import java.util.Set;

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

public class PluginListener implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(PluginListener.class);

	public static final String modulesDir = "de/csgis/geobricks/webapp/modules";

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

		context.setAttribute(Geobricks.DESCRIPTORS_ATTRIBUTE,
				descriptors.toArray(new PluginDescriptor[descriptors.size()]));
		context.setAttribute(Geobricks.CONFIGURATORS_ATTRIBUTE, configurators
				.toArray(new CustomConfigurator[configurators.size()]));
	}

	public void processEntries(Set<String> entries, PluginDescriptor descriptor) {
		for (String entry : entries) {
			if (entry.startsWith(modulesDir) && entry.endsWith(".css")) {
				descriptor.getStyles().add(
						"modules/" + entry.substring(modulesDir.length() + 1));
			} else {
				if (entry.startsWith(modulesDir) && entry.endsWith(".js")) {
					String moduleName = entry.substring(
							modulesDir.length() + 1, entry.length() - 3);
					descriptor.getModules().add(moduleName);
				}
			}
		}
	}

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
			Set<String> styles = descriptor.getStyles();
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
