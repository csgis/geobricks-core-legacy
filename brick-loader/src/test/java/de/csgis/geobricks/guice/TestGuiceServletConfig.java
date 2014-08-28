package de.csgis.geobricks.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class TestGuiceServletConfig extends GuiceServletContextListener {
	private static Injector injector;

	@Override
	protected Injector getInjector() {
		if (injector == null) {
			injector = Guice.createInjector(new RuntimeModule(),
					new ClientServletModule());
		}
		return injector;
	}
}