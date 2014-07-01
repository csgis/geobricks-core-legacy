package de.csgis.geobricks.servlet.client;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
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
		ServletContext context = request.getServletContext();
		String id = context.getInitParameter("geobricks-app-id");
		if (id == null) {
			id = context.getContextPath();
			if (id.startsWith("/")) {
				id = id.substring(1);
			}
		}
		request.setAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE, id);

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
