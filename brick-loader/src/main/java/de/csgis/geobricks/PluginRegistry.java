package de.csgis.geobricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.csgis.geobricks.addressSearch.AddressSearchPlugin;
import de.csgis.geobricks.baseLayer.BaseLayerPlugin;
import de.csgis.geobricks.layerList.LayerListPlugin;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.olmap.OLMapPlugin;
import de.csgis.geobricks.title.TitlePlugin;

public class PluginRegistry {
	private ArrayList<PluginDescriptor> plugins = new ArrayList<>();

	public PluginRegistry() {
		plugins.add(new OLMapPlugin());
		plugins.add(new LayoutPlugin());
		plugins.add(new TitlePlugin());
		plugins.add(new LayerListPlugin());
		plugins.add(new AddressSearchPlugin());
		plugins.add(new BaseLayerPlugin());
	}

	public NonRequireDependency[] getNonRequireDependencies() {
		ArrayList<NonRequireDependency> ret = new ArrayList<>();
		ret.add(new NonRequireDependency("jquery", "jslib/jquery-1.11.0.min"));
		ret.add(new NonRequireDependency("async", "jslib/async"));
		for (PluginDescriptor descriptor : plugins) {
			Collections.addAll(ret, descriptor.getNonRequireDependencies());
		}

		return ret.toArray(new NonRequireDependency[ret.size()]);
	}

	public ClientModuleConfiguration[] getClientModuleConfiguration() {
		ArrayList<ClientModuleConfiguration> ret = new ArrayList<>();
		for (PluginDescriptor descriptor : plugins) {
			Collections.addAll(ret, descriptor.getClientModuleConfiguration());
		}

		return ret.toArray(new ClientModuleConfiguration[ret.size()]);
	}

	public String[] getStyleSheets() {
		ArrayList<String> ret = new ArrayList<String>();
		for (PluginDescriptor descriptor : plugins) {
			Collections.addAll(ret, descriptor.getStyleSheets());
		}

		return ret.toArray(new String[ret.size()]);
	}

	public PluginDescriptor getPlugin(String name) {
		for (PluginDescriptor plugin : plugins) {
			if (plugin.getName().equals(name)) {
				return plugin;
			}
		}

		return null;
	}

	public List<PluginDescriptor> getPlugins() {
		return plugins;
	}
}
