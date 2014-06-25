package de.csgis.geobricks.servlet.client;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import de.csgis.geobricks.Geobricks;

@Singleton
public class GetApplicationIdFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String app = request.getServletContext().getContextPath();
		if (app.startsWith("/")) {
			app = app.substring(1);
		}

		request.setAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE, app);
		chain.doFilter(request, response);

	}

	@Override
	public void destroy() {
	}
}
