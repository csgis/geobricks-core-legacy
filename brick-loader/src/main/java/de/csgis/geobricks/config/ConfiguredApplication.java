package de.csgis.geobricks.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

@Singleton
public class ConfiguredApplication implements ServletContextListener {
	public static final String ATTR_PLUGINS_CONF = "plugins-conf";

	private static final Logger logger = Logger
			.getLogger(ConfiguredApplication.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			configureContext(sce.getServletContext());
		} catch (Exception e) {
			// The application will not be shown so we need to tell at least
			// the developer
			logger.error("The application could not be loaded", e);
		}
	}

	public void configureContext(ServletContext context) throws IOException {
		PluginDescriptor[] descriptors = (PluginDescriptor[]) context
				.getAttribute(Geobricks.DESCRIPTORS_ATTRIBUTE);

		InputStream stream = context
				.getResourceAsStream("/WEB-INF/conf/gbapp-conf.json");
		String json = IOUtils.toString(stream);
		stream.close();

		final List<String> pluginIdsOrdered = new ArrayList<String>();

		// Default plugin configurations when empty
		JSONObject pluginConfigurations = JSONObject.fromObject(json);
		for (Object key : pluginConfigurations.keySet()) {
			String pluginId = key.toString();

			pluginIdsOrdered.add(pluginId);

			JSONObject pluginConf = pluginConfigurations
					.getJSONObject(pluginId);
			JSONObject defaultConf = getDefaultConfiguration(descriptors,
					pluginId);
			if (pluginConf.isEmpty() && defaultConf != null) {
				pluginConfigurations.put(pluginId, defaultConf);
			}
		}
		context.setAttribute(ATTR_PLUGINS_CONF, pluginConfigurations);

		Arrays.sort(descriptors, new Comparator<PluginDescriptor>() {
			@Override
			public int compare(PluginDescriptor o1, PluginDescriptor o2) {
				return pluginIdsOrdered.indexOf(o1.getId())
						- pluginIdsOrdered.indexOf(o2.getId());
			}
		});
		context.setAttribute(Geobricks.DESCRIPTORS_ATTRIBUTE, descriptors);

		// Configure Geobricks configuration directory
		String conf = System.getProperty("GEOBRICKS_CONF_DIR");

		// If the system property is not specified, the configuration
		// is directly in the WEB-INF/default_config directory
		String appConf = context.getRealPath("/") + File.separator + "WEB-INF"
				+ File.separator + "default_config";
		if (conf != null && new File(conf).exists()) {
			// If the system property is specified and the app subdirectory
			// exists,
			// the configuration is in GEOBRICKS_CONF_DIR/<app>/
			String appId = getApplicationId(context);
			File appDir = new File(conf, appId);
			if (appDir.exists()) {
				appConf = conf + File.separator + appId;
			}
		}
		context.setAttribute(Geobricks.CONF_DIR_ATTRIBUTE, appConf);
	}

	private JSONObject getDefaultConfiguration(PluginDescriptor[] descriptors,
			String id) {
		for (PluginDescriptor descriptor : descriptors) {
			if (descriptor.getId().equals(id)) {
				return descriptor.getDefaultConfiguration();
			}
		}
		return null;
	}

	public String getApplicationId(ServletContext context) {
		String id = context.getInitParameter("geobricks-app-id");
		if (id == null) {
			id = context.getContextPath();
			if (id.startsWith("/")) {
				id = id.substring(1);
			}
		}
		return id;
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing
	}
}
