package de.csgis.geobricks.config;

import java.util.Set;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

/**
 * Scans the current context and returns the set of module entries and the
 * plugin conofiguration for each plugin found.
 * 
 * @author vicgonco
 * 
 */
public interface PluginScanner {
	String MODULES_PATH = "webapp/modules";

	/**
	 * Initializes the scanner.
	 * 
	 * @param context
	 *            The current servlet context.
	 */
	void init(ServletContext context);

	/**
	 * Determines if there are more plugins available or not.
	 * 
	 * @return <code>true</code> if there are more plugins to obtain,
	 *         <code>false</code> otherwise.
	 */
	boolean next();

	/**
	 * Set of module entries within the plugin. It will return all the files
	 * under {@link #MODULES_PATH}. Not recursive.
	 * 
	 * @return The set of module entries.
	 */
	Set<String> entries();

	/**
	 * Returns the JSON plugin configuration defined in
	 * <i>/conf/&lt;plugin&gt;-pluginconf.json</i>.
	 * 
	 * @return
	 */
	JSONObject pluginConf();
}
