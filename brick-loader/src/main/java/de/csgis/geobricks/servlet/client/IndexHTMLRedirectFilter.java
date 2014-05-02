package de.csgis.geobricks.servlet.client;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class IndexHTMLRedirectFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// do nothing
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String uri = ((HttpServletRequest) request).getRequestURI();
		if (!uri.endsWith("/")) {
			uri += "/";
		}
		HttpServletResponse resp = (HttpServletResponse) response;
		resp.sendRedirect(uri);
	}

	@Override
	public void destroy() {
		// do nothing
	}
}
