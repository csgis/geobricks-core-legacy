package de.csgis.geobricks.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginDescriptorReader;

public class PluginDependenciesConfigFilter implements ConfigFilter {

	private JSONObject modified, lastConfig;

	private PluginDescriptorReader reader;

	public PluginDependenciesConfigFilter(PluginDescriptorReader reader) {
		this.reader = reader;
	}

	@Override
	public JSONObject modifyConfig(JSONObject config,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (this.lastConfig != config) {
			this.modified = JSONObject.fromObject(config);

			// Get all plugins, including dependencies
			List<String> pluginIds = new ArrayList<String>();
			for (Object key : this.modified.keySet()) {
				pluginIds.add(key.toString());
			}
			PluginDescriptor[] descriptors = reader.getDescriptors(pluginIds);
			Set<PluginDescriptor> plugins = new HashSet<PluginDescriptor>();
			for (PluginDescriptor descriptor : descriptors) {
				plugins.add(descriptor);
				getDependencies(descriptor, plugins);
			}

			for (PluginDescriptor plugin : plugins) {
				if (!this.modified.has(plugin.getId())) {
					this.modified.put(plugin.getId(), new JSONObject());
				}
			}
		}

		return this.modified;
	}

	private Set<PluginDescriptor> getDependencies(PluginDescriptor descriptor,
			Set<PluginDescriptor> deps) {
		for (PluginDescriptor dep : descriptor.getDependencies()) {
			deps.add(dep);
			getDependencies(dep, deps);
		}

		return deps;
	}
}
