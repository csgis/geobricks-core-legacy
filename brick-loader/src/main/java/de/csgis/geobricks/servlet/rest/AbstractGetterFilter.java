package de.csgis.geobricks.servlet.rest;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractGetterFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String path = ((HttpServletRequest) request).getRequestURI();
		String[] parts = path.split(Pattern.quote("/"));
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			if (part.equals(getPreviousSegment()) && i < parts.length - 1) {
				request.setAttribute(getAttributeName(), parts[i + 1]);
				break;
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * Returns the name of the previous segment to get
	 * 
	 * @return
	 */
	protected abstract String getPreviousSegment();

	/**
	 * Returns the name of the attribute where the part name will be storde
	 * 
	 * @return
	 */
	protected abstract String getAttributeName();

	@Override
	public void destroy() {
	}
}