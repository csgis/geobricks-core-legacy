package de.csgis.geobricks;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Puts the name of the application as an attribute in the request
 * 
 * @author fergonco
 */
@Singleton
public class AppGetterFilter implements Filter {

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
			if (part.equals(Geobricks.APPS_ROOT)) {
				request.setAttribute(Geobricks.APPNAME_HTTP_ATTRIBUTE,
						parts[i + 1]);
				break;
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
