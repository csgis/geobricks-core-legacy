package de.csgis.geobricks;

public class NonRequireDependency {

	private String name;
	private String path;

	/**
	 * Create a new {@link NonRequireDependency}
	 * 
	 * @param name
	 *            Name used to reference the library
	 * @param path
	 *            path relative to the root of the application, without the
	 *            leading /
	 */
	public NonRequireDependency(String name, String path) {
		if (path.trim().startsWith("/")) {
			throw new IllegalArgumentException(path);
		}
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}
}
