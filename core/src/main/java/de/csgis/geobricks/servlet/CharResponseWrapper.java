package de.csgis.geobricks.servlet;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * {@link HttpServletResponseWrapper} for obtaining the response content as a
 * string. It only works for the {@link #getWriter()} method, not
 * {@link #getOutputStream()}.
 * 
 * @author vicgonco
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {
	private CharArrayWriter output;

	public String toString() {
		return output.toString();
	}

	public CharResponseWrapper(HttpServletResponse response) {
		super(response);
		output = new CharArrayWriter();
	}

	public PrintWriter getWriter() {
		return new PrintWriter(output);
	}
}