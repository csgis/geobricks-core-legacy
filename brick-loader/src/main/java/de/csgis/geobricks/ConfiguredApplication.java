package de.csgis.geobricks;

import java.io.InputStream;
import java.util.Iterator;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.inject.Injector;

import de.csgis.geobricks.restapi.ApplicationPlugins;
import de.csgis.geobricks.restapi.Applications;

@Singleton
public class ConfiguredApplication implements ServletContextListener {
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

			String appId = WebAppUtils.getApplicationId(servletContext);
			Injector injector = (Injector) servletContext
					.getAttribute(Injector.class.getCanonicalName());
			Applications applications = injector
					.getInstance(Applications.class);
			applications.put(appId);
			ApplicationPlugins applicationPlugins = applications
					.getApplicationPlugins(appId);

			JSONObject plugins = (JSONObject) JSONSerializer.toJSON(jsonText);
			Iterator<?> pluginIdIterator = plugins.keys();
			while (pluginIdIterator.hasNext()) {
				String id = (String) pluginIdIterator.next();
				JSONObject pluginConfiguration = plugins.getJSONObject(id);
				if (!pluginConfiguration.isEmpty()) {
					applicationPlugins.put(id, pluginConfiguration.toString());
				} else {
					applicationPlugins.put(id);
				}
			}
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
