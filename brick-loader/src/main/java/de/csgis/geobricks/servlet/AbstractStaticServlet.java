package de.csgis.geobricks.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Abstract servlet to serve static content. It relies on some abstract methods
 * to obtain resource string representation and stream corresponding to an URI.
 * 
 * @author vicgonco
 * 
 */
public abstract class AbstractStaticServlet extends AbstractGeobricksServlet {
	private static final List<String> TEXT_EXTS = Arrays.asList(".js", ".css",
			".html", ".htm", ".txt");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI().substring(
				getServletContext().getContextPath().length() + 1);

		String resource;
		InputStream stream;

		try {
			resource = getResource(uri);
			setContentTypeAndEncoding(resp, resource);
			stream = getResourceAsStream(resource);
		} catch (IOException e) {
			throw new HTTPCodeServletException(e,
					HttpServletResponse.SC_NOT_FOUND);
		}

		try {
			String ext = resource.toLowerCase().substring(
					resource.lastIndexOf('.'));
			if (TEXT_EXTS.contains(ext)) {
				IOUtils.copy(stream, resp.getWriter());
			} else {
				IOUtils.copy(stream, resp.getOutputStream());
			}
		} finally {
			stream.close();
		}
	}

	/**
	 * Sets the corresponding content type and character encoding on the
	 * response.
	 * 
	 * @param resp
	 *            The response to set the content type and character encoding.
	 * @param uri
	 *            The URI to determine the content type and character encoding.
	 */
	void setContentTypeAndEncoding(HttpServletResponse resp, String uri) {
		if (uri.endsWith(".js")) {
			resp.setContentType("application/javascript");
			resp.setCharacterEncoding("UTF-8");
		} else if (uri.endsWith(".html")) {
			resp.setContentType("text/html");
			resp.setCharacterEncoding("UTF-8");
		} else if (uri.endsWith(".css")) {
			resp.setContentType("text/css");
			resp.setCharacterEncoding("UTF-8");
		} else if (uri.endsWith(".svg")) {
			resp.setContentType("image/svg+xml");
			resp.setCharacterEncoding("UTF-8");
		}
	}

	/**
	 * Returns the local resource that matches the given URI.
	 * 
	 * @param uri
	 *            The URI to check. It will only contain the relative path to
	 *            the resource, excluding the context path.
	 * @return The string representation of the required resource. It will be
	 *         used to call {@link #getResourceAsStream(String)} in order to get
	 *         the resource stream.
	 * @throws IOException
	 *             if the resource does not exist or cannot be accessed.
	 */
	protected abstract String getResource(String uri) throws IOException;

	/**
	 * Returns the specified resource as stream.
	 * 
	 * @param resource
	 *            The resource to obtain, as returned by
	 *            {@link #getResource(String)}.
	 * @return The resource as stream.
	 * @throws IOException
	 *             if the resource does not exist or cannot be accessed.
	 */
	protected abstract InputStream getResourceAsStream(String resource)
			throws IOException;
}
