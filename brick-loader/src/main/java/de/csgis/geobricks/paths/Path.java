package de.csgis.geobricks.paths;

public class Path {

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

	public Path apps() {
		return copy().append("/apps");
	}

	public Path any() {
		return copy().append("/.*");
	}

	public Path plugins() {
		return copy().append("/plugins");
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

	public Path file(String fileName) {
		return copy().append("/" + fileName);
	}

	private Path append(String segment) {
		path.append(segment);
		return this;
	}

}
