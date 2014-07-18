package de.csgis.geobricks;

import java.io.InputStream;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

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
		ServletContext servletContext = sce.getServletContext();
		try {
			InputStream stream = servletContext
					.getResourceAsStream("/WEB-INF/conf/gbapp-conf.json");
			String jsonText = IOUtils.toString(stream);
			stream.close();

			JSONObject pluginConfigurations = (JSONObject) JSONSerializer
					.toJSON(jsonText);
			for (Object key : pluginConfigurations.keySet()) {
				String pluginId = key.toString();
				JSONObject pluginConf = pluginConfigurations
						.getJSONObject(pluginId);
				if (pluginConf.isEmpty()) {
					Injector injector = (Injector) servletContext
							.getAttribute(Injector.class.getCanonicalName());
					PluginRegistry registry = injector
							.getInstance(PluginRegistry.class);
					PluginDescriptor descriptor = registry.getPlugin(pluginId);
					String defaultConf = descriptor.getDefaultConfiguration();
					if (defaultConf != null) {
						pluginConfigurations.put(pluginId,
								JSONObject.fromObject(defaultConf));
					}
				}
			}

			servletContext
					.setAttribute(ATTR_PLUGINS_CONF, pluginConfigurations);
		} catch (Exception e) {
			// The application will not be shown so we need to tell at least
			// the developer
			logger.error("The application could not be loaded", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
