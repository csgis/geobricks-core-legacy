package de.csgis.geobricks;

public interface Geobricks {
	// Servlet init parameters
	String INIT_PARAM_ID = "geobricks-app-id";

	// Environment variables
	String PROP_GEOBRICKS_CONF = "GEOBRICKS_CONF_DIR";

	// Servlet attributes
	String ATTR_CONF_DIR = "de.csgis.geobricks.conf_dir";
	String ATTR_APP_PROPERTIES = "de.csgis.geobricks.app_properties";
	String ATTR_PLUGINS_CONF = "de.csgis.geobricks.plugins-conf";
	String ATTR_PLUGINS_DESC = "de.csgis.geobricks.plugin_descriptors";
}
