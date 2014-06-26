package de.csgis.geobricks.servlet;

public abstract class AbstractServletDescriptor<T> {
	private String regex;
	private String[] regexes;
	private Class<? extends T> clazz;

	public AbstractServletDescriptor(Class<? extends T> filterClass,
			String regex, String... regexes) {
		this.regex = regex;
		this.regexes = regexes;
		this.clazz = filterClass;
	}

	public Class<? extends T> getServletClass() {
		return clazz;
	}

	public String getRegex() {
		return regex;
	}

	public String[] getRegexes() {
		return regexes;
	}
}
