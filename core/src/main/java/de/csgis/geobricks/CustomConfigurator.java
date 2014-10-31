package de.csgis.geobricks;

import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public interface CustomConfigurator {
	void config(HttpServletRequest request, HttpServletResponse response,
			JSONObject staticConfig, String confDir);

	/**
	 * <p>
	 * Gets the mapping for the plugin filters. Keys are regexes and values are
	 * filter classes.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> This is intented to configure filters for URLs that are
	 * already managed by the brick-loader (/, /modules/*, ...). If you want to
	 * filter a custom URL that is not managed by the brick-loader you can
	 * simply use the <code>web-fragment.xml</code> file.
	 * </p>
	 * 
	 * @return The regex -> filter mapping.
	 */
	Map<String, Class<? extends Filter>> getFilters();
}
