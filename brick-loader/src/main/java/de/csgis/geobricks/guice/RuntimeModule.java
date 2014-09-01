package de.csgis.geobricks.guice;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.csgis.geobricks.config.PluginScanner;
import de.csgis.geobricks.config.JarPluginScanner;

public class RuntimeModule implements Module {
	@Override
	public void configure(Binder binder) {
		binder.bind(PluginScanner.class).to(JarPluginScanner.class);
	}
}
