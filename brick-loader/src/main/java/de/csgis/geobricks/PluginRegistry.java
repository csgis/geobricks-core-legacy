package de.csgis.geobricks;

import java.util.ArrayList;
import java.util.Collections;

import de.csgis.geobricks.olmap.OLMapPlugin;

public class PluginRegistry {

	private ArrayList<PluginDescriptor> plugins = new ArrayList<>();

	public PluginRegistry() {
		plugins.add(new OLMapPlugin());
	}

	public NonRequireDependency[] getNonRequireDependencies() {
		ArrayList<NonRequireDependency> ret = new ArrayList<>();
		ret.add(new NonRequireDependency("jquery", "jslib/jquery"));
		for (PluginDescriptor descriptor : plugins) {
			Collections.addAll(ret, descriptor.getNonRequireDependencies());
		}

		return ret.toArray(new NonRequireDependency[ret.size()]);
	}
}
