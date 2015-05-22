package de.csgis.geobricks;

public interface Geobricks {
	// Servlet init parameters
	String INIT_PARAM_ID = "geobricks-app-id";

	// Environment variables
	String PROP_GEOBRICKS_CONF = "GEOBRICKS_CONF_DIR";

	// Servlet attributes
	String ATTR_CONFIG = "de.csgis.geobricks.config";

	// Request attributes
	String ATTR_ROLE = "de.csgis.geobricks.role";
}
