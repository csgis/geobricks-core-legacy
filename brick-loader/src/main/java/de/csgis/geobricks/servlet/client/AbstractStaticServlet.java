package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.csgis.geobricks.Path;
import de.csgis.geobricks.servlet.HTTPCodeServletException;

/**
 * Serves static content from the resources space, starting to look in its own
 * package and in the folder passed to the constructor.
 * 
 * @author fergonco
 */
public abstract class AbstractStaticServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String folder;
	private String resource;

	public AbstractStaticServlet(String folder) {
		this(folder, null);
	}

	public AbstractStaticServlet(String folder, String resource) {
		this.folder = folder != null ? folder : "";
		this.resource = resource;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String requestURI = req.getRequestURI();
		InputStream resourceStream = getResourceStream(requestURI);

		try {
			setContentTypeAndEncoding(resp, requestURI);
			write(resourceStream, resp);
		} finally {
			resourceStream.close();
		}
	}

	public void setContentTypeAndEncoding(HttpServletResponse resp, String uri) {
		String resource = getResource(uri);
		if (resource.endsWith(".js")) {
			resp.setContentType("application/javascript");
			resp.setCharacterEncoding("UTF-8");
		} else if (resource.endsWith(".html")) {
			resp.setContentType("text/html");
			resp.setCharacterEncoding("UTF-8");
		} else if (resource.endsWith(".css")) {
			resp.setContentType("text/css");
			resp.setCharacterEncoding("UTF-8");
		}
	}

	public String getResource(String uri) {
		if (this.resource != null) {
			return this.resource;
		} else {
			return uri.substring(uri.indexOf(folder) + folder.length() + 1);
		}
	}

	public InputStream getResourceStream(String uri)
			throws HTTPCodeServletException {
		String resource = getResource(uri);
		InputStream resourceStream = Path.root.file(folder).file(resource)
				.getResourceAsStream();

		if (resourceStream == null) {
			throw new HTTPCodeServletException("Cannot find resource:"
					+ resource, HttpServletResponse.SC_NOT_FOUND);
		}

		return resourceStream;
	}

	protected abstract void write(InputStream stream,
			HttpServletResponse response) throws IOException;
}
