package de.csgis.geobricks;

import javax.servlet.ServletContextEvent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

public class GeobricksGuiceServletConfig extends GuiceServletContextListener {

	private class GeobricksServletModule extends ServletModule {
		@Override
		protected void configureServlets() {
			String apps = Geobricks.APPS_ROOT;

			// Admin REST API
			String adminBase = "/" + Geobricks.ADMIN_ROOT + "/";
			String adminAppsBase = adminBase + Geobricks.APPS_ROOT + "/";
			String adminPluginList = adminBase + Geobricks.PLUGINS_ROOT;
			String adminAppPlugins = adminAppsBase + ".*/"
					+ Geobricks.PLUGINS_ROOT;

			serveRegex(adminAppPlugins, adminAppPlugins + "/.*").with(
					PluginsServlet.class);
			serve(adminPluginList).with(PluginListServlet.class);
			serve(adminAppsBase + "*").with(GetApplicationServlet.class);

			filter(adminAppsBase + "*").through(AppGetterFilter.class);
			filterRegex(adminAppPlugins + ".*").through(
					PluginGetterFilter.class);
			filter(adminBase + "*").through(OutputFilter.class);

			// Client Requests
			serveRegex("/" + apps + "/.*/jslib/.*").with(
					JslibStaticServlet.class);
			serveRegex("/" + apps + "/.*/modules/.*").with(
					ModulesStaticServlet.class);
			serveRegex("/" + apps + "/.*/config.js").with(ConfigServlet.class);
		}
	}

	private Module moduleInstance;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		String moduleClassName = servletContextEvent.getServletContext()
				.getInitParameter("guice-module-class");
		try {
			moduleInstance = (Module) Class.forName(moduleClassName)
					.newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			/*
			 * Nothing will work if this is wrong, so let's crash properly
			 */
			throw new RuntimeException(e);
		}

		super.contextInitialized(servletContextEvent);
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new GeobricksServletModule(),
				moduleInstance);
	}

}