package de.csgis.geobricks.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.JSONUtils;
import de.csgis.geobricks.PluginDescriptor;

/**
 * Applies the default plugin configurations (
 * <code>&lt;plugin&gt;-conf.json</code>) to the config request.
 * 
 * @author vicgonco
 */
public class PluginDefaultsConfigHandler implements ConfigHandler {
	private JSONObject modified, lastConfig;

	private PluginDescriptor[] descriptors;

	public PluginDefaultsConfigHandler(PluginDescriptor[] descriptors) {
		this.descriptors = descriptors;
	}

	@Override
	public JSONObject modifyConfig(JSONObject config,
			HttpServletRequest request, HttpServletResponse response) {
		if (this.lastConfig != config) {
			this.modified = JSONObject.fromObject(config);

			for (Object key : config.keySet()) {
				String pluginId = key.toString();
				JSONObject pluginConf = config.getJSONObject(pluginId);
				JSONObject defaultConf = getDefaultConfiguration(pluginId);
				if (defaultConf != null) {
					JSONObject merged = JSONUtils
							.merge(defaultConf, pluginConf);
					this.modified.put(pluginId, merged);
				}
			}

			this.lastConfig = config;
		}

		return this.modified;
	}

	/**
	 * Get the default configuration for the specified plugin.
	 * 
	 * @param id
	 *            The identifier of the required plugin.
	 * @return The default configuration of the plugin or <code>null</code> if
	 *         the plugin cannot be found.
	 */
	private JSONObject getDefaultConfiguration(String id) {
		for (PluginDescriptor descriptor : this.descriptors) {
			if (descriptor.getId().equals(id)) {
				return descriptor.getDefaultConfiguration();
			}
		}
		return null;
	}
}
