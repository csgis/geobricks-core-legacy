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
			// Admin REST API
			serveRegex(Geobricks.root.rest().apps().any().plugins().path(),
					Geobricks.root.rest().apps().any().plugins().any().path())
					.with(PluginsServlet.class);
			serve(Geobricks.root.rest().plugins().path()).with(
					PluginListServlet.class);
			serveRegex(Geobricks.root.rest().apps().path(),
					Geobricks.root.rest().apps().any().path()).with(
					GetApplicationServlet.class);

			filterRegex(Geobricks.root.apps().any().path(),
					Geobricks.root.rest().apps().any().path()).through(
					AppGetterFilter.class);
			filterRegex(
					Geobricks.root.rest().apps().any().plugins().any().path())
					.through(PluginGetterFilter.class);
			filterRegex(Geobricks.root.rest().any().path()).through(
					OutputFilter.class);

			// Client Requests
			serveRegex(Geobricks.root.apps().any().jslib().any().path()).with(
					JslibStaticServlet.class);
			serveRegex(Geobricks.root.apps().any().module("main.js").path())
					.with(MainModulesStaticServlet.class);
			serveRegex(Geobricks.root.apps().any().modules().any().path())
					.with(ModulesStaticServlet.class);
			serveRegex(Geobricks.root.apps().any().file("config.js").path())
					.with(ConfigServlet.class);
			serveRegex(Geobricks.root.apps().any().path()).with(
					RealGetApplicationServlet.class);
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