package de.csgis.geobricks.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.JSONContentProvider;
import de.csgis.geobricks.JSONUtils;

/**
 * {@link ConfigFilter} for role specific configuration. Roles are defined by
 * separate <code>&lt;role&gt;.json</code> files in the {@link #ROLE_DIR}
 * directory, containing application descriptors (same format as
 * <code>gbapp-conf.json</code> files).
 * 
 * @author vicgonco
 */
public class RoleSpecificConfigFilter implements ConfigFilter {
	public static final String ROLE_DIR = "role_conf";

	private JSONContentProvider contents;

	public RoleSpecificConfigFilter(String configDir) {
		this.contents = new JSONContentProvider(configDir + File.separator
				+ ROLE_DIR);
	}

	@Override
	public JSONObject modifyConfig(JSONObject config,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		JSONObject modified = JSONObject.fromObject(config);

		Object attr = request.getAttribute(Geobricks.ATTR_ROLE);
		if (attr == null) {
			return modified;
		}

		String role = attr.toString();
		JSONObject roleSpecificConf = contents.get().get(role);
		if (roleSpecificConf == null) {
			return modified;
		}

		for (Object key : roleSpecificConf.keySet()) {
			String plugin = key.toString();
			JSONObject override = roleSpecificConf.getJSONObject(plugin);
			JSONObject defaultObj = config.getJSONObject(plugin);
			JSONObject merged = JSONUtils.merge(defaultObj, override);
			modified.put(plugin, merged);
		}

		return modified;
	}
}
