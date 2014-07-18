package de.csgis.geobricks.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.restapi.ApplicationNotFoundException;
import de.csgis.geobricks.restapi.Applications;

@Singleton
public class GetApplicationInstanceFilter implements Filter {

	@Inject
	Applications applications;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Object appName = request.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE);

		try {
			request.setAttribute(Geobricks.APP_INSTANCE_HTTP_ATTRIBUTE,
					applications.get(appName.toString()));
		} catch (ApplicationNotFoundException e) {
			// do nothing
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
