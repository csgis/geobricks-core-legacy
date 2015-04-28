package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.InputStream;

import de.csgis.geobricks.Path;

/**
 * Servlet to serve static resources from the classpath.
 * 
 * @author vicgonco
 */
public class ClasspathResourceServlet extends AbstractStaticServlet {
	private String directory;
	private String resource;

	public ClasspathResourceServlet(String directory) {
		this(directory, null);
	}

	public ClasspathResourceServlet(String directory, String resource) {
		this.directory = directory != null ? directory : "";
		this.resource = resource;
	}

	@Override
	protected String getResource(String uri) throws IOException {
		if (this.resource != null) {
			return resource;
		} else if (uri.startsWith(directory)) {
			return uri;
		} else {
			throw new IOException("Cannot find resource '" + uri
					+ "' within folder '" + this.directory + "'");
		}
	}

	@Override
	protected InputStream getResourceAsStream(String resource)
			throws IOException {
		if (this.resource != null && !this.resource.equals(resource)) {
			throw new IOException("Invalid resource: " + resource
					+ ". Expecting: " + this.resource);
		} else if (this.resource == null && !resource.startsWith(directory)) {
			throw new IOException("Cannot find resource '" + resource
					+ "' within folder '" + this.directory + "'");
		}

		InputStream stream = Path.root.file(resource).getResourceAsStream();
		if (stream != null) {
			return stream;
		} else {
			throw new IOException("Cannot find resource: " + resource);
		}
	}
}
