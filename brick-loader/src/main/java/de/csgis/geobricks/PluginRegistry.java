package de.csgis.geobricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.csgis.geobricks.addressSearch.AddressSearchPlugin;
import de.csgis.geobricks.admin.AdminPluginDescriptor;
import de.csgis.geobricks.baseLayer.BaseLayerPlugin;
import de.csgis.geobricks.divstack.DivStackPluginDescriptor;
import de.csgis.geobricks.featureInfo.FeatureInfoPlugin;
import de.csgis.geobricks.layerList.LayerListPlugin;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.olmap.OLMapPlugin;
import de.csgis.geobricks.toolbar.ToolbarPlugin;
import de.csgis.geobricks.ui.UIPluginDescriptor;

public class PluginRegistry {
	private ArrayList<PluginDescriptor> plugins = new ArrayList<>();

	public PluginRegistry() {
		plugins.add(new OLMapPlugin());
		plugins.add(new LayoutPlugin());
		plugins.add(new LayerListPlugin());
		plugins.add(new AddressSearchPlugin());
		plugins.add(new BaseLayerPlugin());
		plugins.add(new FeatureInfoPlugin());
		plugins.add(new UIPluginDescriptor());
		plugins.add(new AdminPluginDescriptor());
		plugins.add(new DivStackPluginDescriptor());
		plugins.add(new ToolbarPlugin());
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

	public List<PluginDescriptor> getPlugins() {
		return plugins;
	}
}
