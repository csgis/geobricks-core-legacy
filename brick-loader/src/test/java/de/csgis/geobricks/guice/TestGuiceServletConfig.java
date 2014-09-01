package de.csgis.geobricks.guice;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;

import de.csgis.geobricks.config.PluginScanner;

public class TestGuiceServletConfig extends GuiceServletContextListener implements
		Module {
	private Injector injector;

	@Override
	protected Injector getInjector() {
		if (injector == null) {
			injector = Guice.createInjector(this, new ClientServletModule());
		}
		return injector;
	}

	@Override
	public void configure(Binder binder) {
		binder.bind(PluginScanner.class).to(TestResourceScanner.class);
	}
}