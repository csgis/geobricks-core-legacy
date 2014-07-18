package de.csgis.geobricks.guice;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.csgis.geobricks.PluginRegistry;

public class RuntimeModule implements Module {
	@Override
	public void configure(Binder binder) {
		binder.bind(PluginRegistry.class).toInstance(new PluginRegistry());
	}
}