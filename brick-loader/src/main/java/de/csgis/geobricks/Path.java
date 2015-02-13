package de.csgis.geobricks;

import java.io.InputStream;

import com.google.inject.servlet.ServletModule;

/**
 * Class that holds all the folder structure of the web applications.
 * 
 * @author fergonco
 */
public class Path {
	public static Path root = new Path("");

	public static final String APPS = "apps";
	public static final String PLUGINS = "plugins";

	protected StringBuilder path;

	public Path(String root) {
		path = new StringBuilder(root);
	}

	/**
	 * A Java-style regex representing the path. It has to be used with
	 * {@link ServletModule#serveRegex} and {@link ServletModule#filterRegex}
	 * instead of {@link ServletModule#serve} and {@link ServletModule#filter}.
	 * 
	 * @return A Java-style regex representing the path.
	 */
	public String path() {
		return path.toString();
	}

	private Path copy() {
		Path ret = new Path(path());
		return ret;
	}

	public Path all() {
		return copy().append(".*");
	}

	public Path jslib() {
		return copy().append("/jslib");
	}

	public Path modules() {
		return copy().append("/modules");
	}

	public Path images() {
		return copy().append("/images");
	}

	public Path styles() {
		return copy().append("/styles");
	}

	public Path theme() {
		return copy().append("/theme");
	}

	public Path module(String moduleName) {
		return copy().append("/modules/" + moduleName);
	}

	public Path file(String fileName) {
		if (fileName != null && fileName.length() > 0) {
			return copy().append("/" + fileName);
		} else {
			return copy();
		}
	}

	private Path append(String segment) {
		path.append(segment);
		return this;
	}

	public InputStream getResourceAsStream() {
		return this.getClass().getResourceAsStream("/webapp" + path());
	}
}
