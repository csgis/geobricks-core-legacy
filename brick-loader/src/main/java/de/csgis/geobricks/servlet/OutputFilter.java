package de.csgis.geobricks.servlet;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Filter for catching exceptions and formatting them into well known server
 * responses (JSON objects with status code and message). In order to configure
 * status code and message properly, throw {@link HTTPCodeServletException}
 * exceptions.
 * 
 * @author vicgonco
 */
@Singleton
public class OutputFilter implements Filter {
	private static final Logger logger = Logger.getLogger(OutputFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// do nothing
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			manageException(e, (HttpServletResponse) response);
		}
	}

	public void manageException(Exception e, HttpServletResponse response)
			throws IOException {
		int statusCode = 500;
		String errorMsg = "Server error";

		if (e instanceof HTTPCodeServletException) {
			statusCode = ((HTTPCodeServletException) e).getStatusCode();
			errorMsg = e.getMessage();
		}

		response.setStatus(statusCode);
		response.setContentType("application/json");
		response.setCharacterEncoding("utf8");

		if (errorMsg != null) {
			response.getWriter().write("{\"message\":\"" + errorMsg + "\"}");
		}

		if (statusCode == 500) {
			logger.error(errorMsg, e);
		} else {
			String msg = errorMsg != null ? errorMsg : "";
			logger.info("(" + statusCode + ") " + msg);
			logger.debug(e);
		}
	}

	@Override
	public void destroy() {
		// do nothing
	}
}
