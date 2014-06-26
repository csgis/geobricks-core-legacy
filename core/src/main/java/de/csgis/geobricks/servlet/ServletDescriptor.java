package de.csgis.geobricks.servlet;

import javax.servlet.http.HttpServlet;

public class ServletDescriptor extends AbstractServletDescriptor<HttpServlet> {
	public ServletDescriptor(Class<? extends HttpServlet> servletClass,
			String regex, String... regexes) {
		super(servletClass, regex, regexes);
	}
}