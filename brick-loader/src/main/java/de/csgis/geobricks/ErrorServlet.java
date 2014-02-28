package de.csgis.geobricks;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// Method to handle GET method request.
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Analyze the servlet exception
		Throwable throwable = (Throwable) request
				.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request
				.getAttribute("javax.servlet.error.status_code");
		String errorMsg = (String) request
				.getAttribute("javax.servlet.error.message");
		String servletName = (String) request
				.getAttribute("javax.servlet.error.servlet_name");
		if (servletName == null) {
			servletName = "Unknown";
		}
		String requestUri = (String) request
				.getAttribute("javax.servlet.error.request_uri");
		if (requestUri == null) {
			requestUri = "Unknown";
		}

		if (throwable == null && statusCode == null) {
			PrintWriter out = response.getWriter();
			out.println("<h2>Error information is missing</h2>");
		} else {
			if (throwable instanceof HTTPCodeServletException) {
				response.setStatus(((HTTPCodeServletException) throwable)
						.getStatusCode());
			} else {
				response.setStatus(500);
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("utf8");
			response.getWriter().write("{\"message\":\"" + errorMsg + "\"}");
		}
	}
}
