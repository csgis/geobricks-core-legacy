package de.csgis.geobricks.servlet;

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
		setStatusCode(statusCode);
	}

	public HTTPCodeServletException(Throwable cause, int statusCode) {
		super(cause.getMessage(), cause);
		setStatusCode(statusCode);
	}

	private void setStatusCode(int statusCode) {
		if (statusCode == 204 && getMessage() != null) {
			throw new IllegalArgumentException("204 -> no content");
		}
		this.statusCode = statusCode;
	}

	public HTTPCodeServletException(int statusCode) {
		setStatusCode(statusCode);
	}

	public HTTPCodeServletException(String message, Throwable cause,
			int statusCode) {
		super(message, cause);
		setStatusCode(statusCode);
	}

	public int getStatusCode() {
		return statusCode;
	}
}
