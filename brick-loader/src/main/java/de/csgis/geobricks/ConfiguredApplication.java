package de.csgis.geobricks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.inject.Injector;

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
		Injector injector = (Injector) context.getAttribute(Injector.class
				.getCanonicalName());
		PluginRegistry registry = injector.getInstance(PluginRegistry.class);

		InputStream stream = context
				.getResourceAsStream("/WEB-INF/conf/gbapp-conf.json");
		String json = IOUtils.toString(stream);
		stream.close();

		// Default plugin configurations when empty
		JSONObject pluginConfigurations = JSONObject.fromObject(json);
		for (Object key : pluginConfigurations.keySet()) {
			String pluginId = key.toString();
			JSONObject pluginConf = pluginConfigurations
					.getJSONObject(pluginId);
			String defaultConf = registry.getPlugin(pluginId)
					.getDefaultConfiguration();
			if (pluginConf.isEmpty() && defaultConf != null) {
				pluginConfigurations.put(pluginId,
						JSONObject.fromObject(defaultConf));
			}
		}
		context.setAttribute(ATTR_PLUGINS_CONF, pluginConfigurations);

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
			String appId = WebAppUtils.getApplicationId(context);
			File appDir = new File(conf, appId);
			if (appDir.exists()) {
				appConf = conf + File.separator + appId;
			}
		}
		context.setAttribute(Geobricks.CONF_DIR_ATTRIBUTE, appConf);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
