package de.csgis.geobricks.restapi;

/**
 * The plugin is not available in this application catalog
 * 
 * @author fergonco
 */
public class PluginNotAvailableException extends Exception {

	private static final long serialVersionUID = 1L;

	public PluginNotAvailableException(String message) {
		super(message);
	}

}
