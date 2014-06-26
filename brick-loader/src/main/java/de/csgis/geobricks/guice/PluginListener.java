package de.csgis.geobricks.guice;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.google.inject.Injector;

import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;

public class PluginListener implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(PluginListener.class);

	private static final String CONTEXT_PARAM_PREFIX = "geobricks-plugin-";

	@Override
	@SuppressWarnings("unchecked")
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();

		Injector injector = (Injector) context.getAttribute(Injector.class
				.getCanonicalName());
		PluginRegistry pluginRegistry = injector
				.getInstance(PluginRegistry.class);

		Enumeration<String> params = context.getInitParameterNames();
		while (params.hasMoreElements()) {
			String param = params.nextElement();
			if (param.startsWith(CONTEXT_PARAM_PREFIX)) {
				String pluginClassName = context.getInitParameter(param);
				try {
					Class<? extends PluginDescriptor> pluginClass = (Class<? extends PluginDescriptor>) Class
							.forName(pluginClassName);
					PluginDescriptor plugin = injector.getInstance(pluginClass);
					pluginRegistry.putPlugin(plugin);
				} catch (ClassNotFoundException e) {
					String plugin = param.substring(CONTEXT_PARAM_PREFIX
							.length());
					logger.error("Cannot add plugin '" + plugin
							+ "'! Skipping.", e);
				}
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing
	}
}
