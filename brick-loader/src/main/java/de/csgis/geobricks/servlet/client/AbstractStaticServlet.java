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

		String resource;
		if (this.resource != null) {
			resource = this.resource;
		} else {
			resource = requestURI.substring(requestURI.indexOf(folder)
					+ folder.length() + 1);
		}

		InputStream resourceStream = Path.root.file(folder).file(resource)
				.getResourceAsStream();

		if (resourceStream == null) {
			throw new HTTPCodeServletException("Cannot find resource:"
					+ resource, HttpServletResponse.SC_NOT_FOUND);
		}

		try {
			write(resourceStream, resp);
		} finally {
			resourceStream.close();
		}
	}

	protected abstract void write(InputStream stream,
			HttpServletResponse response) throws IOException;
}
