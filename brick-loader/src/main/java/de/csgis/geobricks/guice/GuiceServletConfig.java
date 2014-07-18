package de.csgis.geobricks.guice;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceServletConfig extends GuiceServletContextListener {
	private Injector injector;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext context = servletContextEvent.getServletContext();

		String moduleClassName = context.getInitParameter("guice-module-class");

		Module module;
		try {
			module = (Module) Class.forName(moduleClassName).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			/*
			 * Nothing will work if this is wrong, so let's crash properly
			 */
			throw new RuntimeException(e);
		}

		injector = Guice.createInjector(module, new ClientServletModule());

		// This can be used by future listeners to obtain the injector
		context.setAttribute(Injector.class.getCanonicalName(), injector);

		super.contextInitialized(servletContextEvent);
	}

	@Override
	protected Injector getInjector() {
		return injector;
	}
}