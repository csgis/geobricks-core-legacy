package de.csgis.geobricks.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceServletConfig extends GuiceServletContextListener {
	private static Injector injector;

	@Override
	protected Injector getInjector() {
		return injector();
	}

	public static Injector injector() {
		if (injector == null) {
			injector = Guice.createInjector(new ClientServletModule());
		}
		return injector;
	}

	/**
	 * Only for testing purposes.
	 * 
	 * @param injector
	 * @return
	 */
	public static void setInjector(Injector injector) {
		GuiceServletConfig.injector = injector;
	}
}