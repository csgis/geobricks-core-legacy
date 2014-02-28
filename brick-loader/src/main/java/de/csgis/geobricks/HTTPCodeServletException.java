package de.csgis.geobricks;

import javax.servlet.ServletException;

public class HTTPCodeServletException extends ServletException {

	private static final long serialVersionUID = 1L;
	private int statusCode;

	public HTTPCodeServletException(String string, int statusCode) {
		super(string);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
