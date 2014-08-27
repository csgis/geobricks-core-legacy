package de.csgis.geobricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PluginRegistry {
	private Set<PluginDescriptor> plugins = new HashSet<PluginDescriptor>();

	public PluginRegistry() {
	}

	public NonRequireDependency[] getNonRequireDependencies() {
		ArrayList<NonRequireDependency> ret = new ArrayList<>();
		ret.add(new NonRequireDependency("jquery", "jslib/jquery-1.11.0.min"));
		for (PluginDescriptor descriptor : plugins) {
			NonRequireDependency[] nonRequireDependencies = descriptor
					.getNonRequireDependencies();
			if (nonRequireDependencies != null) {
				Collections.addAll(ret, nonRequireDependencies);
			}
		}

		return ret.toArray(new NonRequireDependency[ret.size()]);
	}

	public PluginDescriptor getPlugin(String id) {
		for (PluginDescriptor plugin : plugins) {
			if (plugin.getId().equals(id)) {
				return plugin;
			}
		}

		return null;
	}

	public void putPlugin(PluginDescriptor plugin) {
		plugins.add(plugin);
	}

	public Set<PluginDescriptor> getPlugins() {
		return plugins;
	}
}
