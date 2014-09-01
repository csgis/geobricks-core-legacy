package de.csgis.geobricks.config;

import java.util.Set;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

public interface PluginScanner {
	void init(ServletContext context);

	boolean next();

	Set<String> entries();

	JSONObject pluginConf();
}
