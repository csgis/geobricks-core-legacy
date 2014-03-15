package de.csgis.geobricks;

import de.csgis.geobricks.paths.Path;

public class Geobricks {

	public static final String APPS_ROOT = "apps";
	public static final String PLUGINS_ROOT = "plugins";
	public static final String ADMIN_ROOT = "rest";
	public static final String APP_ID_HTTP_ATTRIBUTE = "de.csgis.geobricks.app_id";
	public static final String PLUGIN_NAME_HTTP_ATTRIBUTE = "de.csgis.geobricks.plugin_name";
	public static Path rest = new Path("/rest");
	public static Path root = new Path("");

}
