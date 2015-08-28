package de.csgis.geobricks.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * Modifies the given config.js response. Among its uses we can find:
 * <ul>
 * <li>Adding dynamic configuration.</li>
 * <li>Adding role-specific plugins and/or configurations.
 * <li>Overriding the default configuration with contents from files on the
 * configuration directory ({@link Config#getConfigDir()}.</li>
 * </ul>
 * 
 * @author vicgonco
 */
public interface ConfigFilter {
	/**
	 * Returns a new JSON object as a result of the modification of the given
	 * object. The returned object <b>must</b> be a different object and the
	 * given object must not be modified.
	 * 
	 * @param config
	 *            The config to process. This object <b>must not</b> be
	 *            modified.
	 * @param request
	 *            The config.js HTTP request.
	 * @param response
	 *            The config.js HTTP response. It may be needed for updating
	 *            cookies.
	 * @throws IOException
	 *             if any I/O error occurs while modifying the configuration.
	 * @return A new object as a result of the modification.
	 */
	JSONObject modifyConfig(JSONObject config, HttpServletRequest request,
			HttpServletResponse response) throws IOException;
}
