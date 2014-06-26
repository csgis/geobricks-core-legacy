package de.csgis.geobricks.servlet;

import javax.servlet.Filter;

public class FilterDescriptor extends AbstractServletDescriptor<Filter> {
	public FilterDescriptor(Class<? extends Filter> filterClass, String regex,
			String... regexes) {
		super(filterClass, regex, regexes);
	}
}
