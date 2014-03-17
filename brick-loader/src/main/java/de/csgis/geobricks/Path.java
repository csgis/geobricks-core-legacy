package de.csgis.geobricks;

import java.io.InputStream;

/**
 * Class that holds all the folder structure of the rest API and the web
 * applications
 * 
 * @author fergonco
 */
public class Path {

	public static final String APPS = "apps";
	public static final String PLUGINS = "plugins";

	protected StringBuilder path;

	public Path(String root) {
		path = new StringBuilder(root);
	}

	public String path() {
		return path.toString();
	}

	private Path copy() {
		Path ret = new Path(path());
		return ret;
	}

	public Path rest() {
		return copy().append("/" + "rest");
	}

	public Path apps() {
		return copy().append("/" + APPS);
	}

	public Path any() {
		return copy().append("/.*");
	}

	public Path plugins() {
		return copy().append("/" + PLUGINS);
	}

	public Path jslib() {
		return copy().append("/jslib");
	}

	public Path modules() {
		return copy().append("/modules");
	}

	public Path module(String moduleName) {
		return copy().append("/modules/" + moduleName);
	}

	public Path app(String appId) {
		return copy().append("/apps/" + appId);
	}

	public Path file(String fileName) {
		return copy().append("/" + fileName);
	}

	private Path append(String segment) {
		path.append(segment);
		return this;
	}

	public InputStream getResourceAsStream() {
		return this.getClass().getResourceAsStream(
				"/de/csgis/geobricks/webapp" + path());
	}

}
