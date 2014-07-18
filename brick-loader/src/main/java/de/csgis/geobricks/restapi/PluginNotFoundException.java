package de.csgis.geobricks.restapi;

public class PluginNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public PluginNotFoundException(String appId, String pluginId) {
		super("Cannot find plugin '" + pluginId + "' for application '" + appId
				+ "'");
	}

}
