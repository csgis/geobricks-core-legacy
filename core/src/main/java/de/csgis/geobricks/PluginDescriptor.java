package de.csgis.geobricks;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public interface PluginDescriptor {

	/**
	 * @return the paths for the plugin dependencies that are not RequireJS
	 *         modules. If a path for the aplication is specified, the library
	 *         must be included as a resource in the classpath. null if none
	 */
	NonRequireDependency[] getNonRequireDependencies();

	/**
	 * @return the configuration that will be passed to the requirejs modules.
	 *         The string must contain a JSON object with one property with the
	 *         name of each module that is to be configured.
	 * 
	 *         It can return null if no configuration is necessary
	 */
	String getDefaultConfiguration();

	/**
	 * @return the paths for the CSS files required by this plugin. null if none
	 */
	String[] getStyleSheets();

	/**
	 * Human readable name to be displayed.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Unique identifier for the plugin.
	 * 
	 * @return
	 */
	String getId();

	/**
	 * Get the requireJS modules in this plugin. null if none.
	 * 
	 * @return
	 */
	String[] getModules();

	void config(HttpServletRequest request, HttpServletResponse response,
			JSONObject staticConfig, String confDir);
}
