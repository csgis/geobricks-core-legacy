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

			serveRegex("/" + apps + "/.*/jslib/.*").with(
					JslibStaticServlet.class);
			serveRegex("/" + apps + "/.*/modules/.*").with(
					ModulesStaticServlet.class);
			serveRegex("/" + apps + "/.*/config.js").with(ConfigServlet.class);
			serveRegex("/" + apps + "/.*/plugins/.*")
					.with(PluginsServlet.class);
			serve("/" + apps + "/*").with(GetApplicationServlet.class);

			/*
			 * Filters
			 */
			filter("/" + apps + "/*").through(OutputFilter.class);
			filter("/" + apps + "/*").through(AppGetterFilter.class);
			filterRegex("/" + apps + "/.*/plugins/.*").through(
					PluginGetterFilter.class);
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