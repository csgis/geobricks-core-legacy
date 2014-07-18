package de.csgis.geobricks.restapi;

public class ApplicationNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ApplicationNotFoundException(String appId) {
		super("Application not found: " + appId);
	}

}
