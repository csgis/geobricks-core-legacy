package de.csgis.geobricks;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class OutputFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
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
				response.getWriter()
						.write("{\"message\":\"" + errorMsg + "\"}");
			}
		}
	}

	@Override
	public void destroy() {
	}

}
