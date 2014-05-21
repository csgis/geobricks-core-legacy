package de.csgis.geobricks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.csgis.geobricks.addressSearch.AddressSearchPlugin;
import de.csgis.geobricks.admin.AdminPluginDescriptor;
import de.csgis.geobricks.baseLayer.BaseLayerPlugin;
import de.csgis.geobricks.layerList.LayerListPlugin;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.login.LoginPlugin;
import de.csgis.geobricks.olmap.OLMapPlugin;
import de.csgis.geobricks.queryInfo.QueryInfoPlugin;
import de.csgis.geobricks.servlet.FilterDescriptor;
import de.csgis.geobricks.servlet.ServletDescriptor;
import de.csgis.geobricks.timeSelector.TimeSelectorPlugin;
import de.csgis.geobricks.ui.UIPluginDescriptor;
import de.csgis.geobricks.vfs.VFSPluginDescriptor;

public class PluginRegistry {
	private ArrayList<PluginDescriptor> plugins = new ArrayList<>();

	public PluginRegistry() {
		plugins.add(new OLMapPlugin());
		plugins.add(new LayoutPlugin());
		plugins.add(new LayerListPlugin());
		plugins.add(new AddressSearchPlugin());
		plugins.add(new BaseLayerPlugin());
		plugins.add(new TimeSelectorPlugin());
		plugins.add(new UIPluginDescriptor());
		plugins.add(new AdminPluginDescriptor());
		plugins.add(new LoginPlugin());
		plugins.add(new TimeSelectorPlugin());
		plugins.add(new VFSPluginDescriptor());
		plugins.add(new QueryInfoPlugin());
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

	public List<FilterDescriptor> getFilters() {
		List<FilterDescriptor> filters = new ArrayList<FilterDescriptor>();
		for (PluginDescriptor plugin : plugins) {
			FilterDescriptor[] pluginFilters = plugin.getFilters();
			if (pluginFilters != null) {
				Collections.addAll(filters, pluginFilters);
			}
		}
		return filters;
	}

	public List<ServletDescriptor> getServlets() {
		List<ServletDescriptor> servlets = new ArrayList<ServletDescriptor>();
		for (PluginDescriptor plugin : plugins) {
			ServletDescriptor[] pluginServlets = plugin.getServlets();
			if (pluginServlets != null) {
				Collections.addAll(servlets, pluginServlets);
			}
		}
		return servlets;
	}
}
