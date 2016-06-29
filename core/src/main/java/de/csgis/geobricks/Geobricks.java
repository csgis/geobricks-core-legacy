package de.csgis.geobricks;

public interface Geobricks {
	// Servlet init parameters
	String INIT_PARAM_ID = "geobricks-app-id";

	// Environment variables
	String PROP_GEOBRICKS_CONF = "GEOBRICKS_CONF_DIR";

	// Request attributes

	// HttpSession user attribute
	String SESSION_ATTR_USER = "de.csgis.geobricks.session.user";
	String SESSION_ATTR_PASS = "de.csgis.geobricks.session.pass";
	String SESSION_ATTR_ROLE = "de.csgis.geobricks.session.role";
}
