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

import de.csgis.geobricks.servlet.HTTPCodeServletException;

@Singleton
public class OutputFilter implements Filter {
	private static final Logger logger = Logger.getLogger(OutputFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			int statusCode = 500;
			String errorMsg = "Server error";
			if (e instanceof HTTPCodeServletException) {
				statusCode = ((HTTPCodeServletException) e).getStatusCode();
				errorMsg = e.getMessage();
			}
			((HttpServletResponse) response).setStatus(statusCode);
			response.setContentType("application/json");
			response.setCharacterEncoding("utf8");
			if (errorMsg != null) {
				String msg = "{\"message\":\"" + errorMsg + "\"}";
				response.getOutputStream().write(msg.getBytes());
			}

			if (statusCode == 500) {
				logger.error(errorMsg, e);
			} else {
				String msg = errorMsg != null ? errorMsg : "";
				logger.info("(" + statusCode + ") " + msg);
				logger.debug(e);
			}
		}
	}

	@Override
	public void destroy() {
	}

}
