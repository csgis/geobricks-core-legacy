package de.csgis.geobricks.servlet;

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

			for (String plugin : overrideConfs.keySet()) {
				JSONObject override = overrideConfs.get(plugin);
				JSONObject defaultObj = config.getJSONObject(plugin);
				JSONObject merged = JSONUtils.merge(defaultObj, override);
				this.modified.put(plugin, merged);
			}
		}

		return this.modified;
	}
}
