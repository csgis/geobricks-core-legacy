package de.csgis.geobricks.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.csgis.commons.JSONUtils;
import de.csgis.geobricks.PluginDescriptor;
import net.sf.json.JSONObject;

/**
 * {@link ConfigFilter} for applying the default plugin configurations (
 * <code>&lt;plugin&gt;-conf.json</code>) to the current configuration.
 * 
 * @author vicgonco
 */
public class PluginDefaultsConfigFilter implements ConfigFilter {
	private JSONObject modified, lastConfig;
	private Config config;

	public PluginDefaultsConfigFilter(Config config) {
		this.config = config;
	}

	@Override
	public JSONObject modifyConfig(JSONObject config,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (this.lastConfig != config) {
			this.modified = JSONObject.fromObject(config);

			for (Object key : config.keySet()) {
				String pluginId = key.toString();
				JSONObject pluginConf = config.getJSONObject(pluginId);
				JSONObject defaultConf = getDefaultConfiguration(pluginId,
						this.config.getPluginDescriptors(config));
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
	private JSONObject getDefaultConfiguration(String id,
			PluginDescriptor[] descriptors) {
		for (PluginDescriptor descriptor : descriptors) {
			if (descriptor.getId().equals(id)) {
				return descriptor.getDefaultConfiguration();
			}
		}
		return null;
	}
}
