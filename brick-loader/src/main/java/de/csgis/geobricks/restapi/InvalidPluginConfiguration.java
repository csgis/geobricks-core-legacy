package de.csgis.geobricks.restapi;

public class InvalidPluginConfiguration extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidPluginConfiguration(String message, Throwable cause) {
		super("Invalid plugin configuration: " + message, cause);
	}

}
