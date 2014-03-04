package de.csgis.geobricks;

import javax.servlet.ServletException;

/**
 * Exception used to send messages to the client. The usage of this exception
 * does not mean that an error is sent a message. The condition of error is
 * defined by the HTTP code passed as a parameter on construction (2xx codes are
 * indicators of success).
 * 
 * @author fergonco
 */
public class HTTPCodeServletException extends ServletException {

	private static final long serialVersionUID = 1L;
	private int statusCode;

	public HTTPCodeServletException(String string, int statusCode) {
		super(string);
		if (statusCode == 204 && string != null) {
			throw new IllegalArgumentException("204 -> no content");
		}
		this.statusCode = statusCode;
	}

	public HTTPCodeServletException(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
