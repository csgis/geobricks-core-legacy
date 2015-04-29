package de.csgis.geobricks.config;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.servlet.Config;

/**
 * Creates a {@link Config} object using the servlet context and adds it as a
 * servlet context attribute.
 * 
 * @author vicgonco
 */
@Singleton
public class ApplicationListener implements ServletContextListener {
	private static final Logger logger = Logger
			.getLogger(ApplicationListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			ServletContext context = sce.getServletContext();
			Config config = new Config();
			config.init(context);
			context.setAttribute(Geobricks.ATTR_CONFIG, config);
		} catch (Exception e) {
			// The application will not be shown so we need to tell at least
			// the developer
			logger.error("The application could not be loaded", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing
	}
}
