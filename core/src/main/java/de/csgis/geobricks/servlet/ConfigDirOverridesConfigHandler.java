package de.csgis.geobricks.servlet;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.JSONContentProvider;
import de.csgis.geobricks.JSONUtils;

/**
 * Overrides the given config with all the <code>*.json</code> file contents on
 * the configuration directory ({@link Config#getConfigDir()}).
 * 
 * @author vicgonco
 */
public class ConfigDirOverridesConfigHandler implements ConfigHandler {
	private JSONContentProvider contents;
	private JSONObject modified, lastConfig;

	public ConfigDirOverridesConfigHandler(String configDir) {
		this.contents = new JSONContentProvider(configDir);
	}

	@Override
	public JSONObject modifyConfig(JSONObject config,
			HttpServletRequest request, HttpServletResponse response) {
		if (this.lastConfig != config) {
			this.modified = JSONObject.fromObject(config);

			Map<String, JSONObject> overrideConfs = this.contents.get();

			for (String key : overrideConfs.keySet()) {
				JSONObject override = overrideConfs.get(key);

				JSONObject plugin = findPluginWithModule(this.modified, key);
				if (plugin != null) {
					// Treat it as a module override
					JSONObject defaultObj = plugin.getJSONObject(key);
					plugin.put(key, JSONUtils.merge(defaultObj, override));
				} else {
					// Treat it as a plugin override
					JSONObject defaultObj = this.modified.getJSONObject(key);
					JSONObject merged = JSONUtils.merge(defaultObj, override);
					this.modified.put(key, merged);
				}
			}
		}

		return this.modified;
	}

	private JSONObject findPluginWithModule(JSONObject config, String module) {
		Iterator<?> it = config.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			JSONObject plugin = config.getJSONObject(key);
			if (plugin.has(module)) {
				return plugin;
			}
		}

		return null;
	}
}
