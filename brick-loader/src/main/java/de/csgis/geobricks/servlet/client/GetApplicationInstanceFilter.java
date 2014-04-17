package de.csgis.geobricks.servlet.client;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PersistenceUtils;
import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.servlet.HTTPCodeServletException;

@Singleton
public class GetApplicationInstanceFilter implements Filter {
	@Inject
	private PersistenceUtils utils;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String appName = request.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();

		Application app = utils.getApplication(appName);
		if (app != null) {
			request.setAttribute(Geobricks.APP_INSTANCE_HTTP_ATTRIBUTE, app);
		} else {
			throw new HTTPCodeServletException("Application not found: "
					+ appName, HttpServletResponse.SC_NOT_FOUND);
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
