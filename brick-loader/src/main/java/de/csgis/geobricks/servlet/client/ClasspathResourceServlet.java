package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.InputStream;

import de.csgis.geobricks.Path;

public class ClasspathResourceServlet extends AbstractStaticServlet {
	private String folder;
	private String resource;

	public ClasspathResourceServlet(String folder) {
		this(folder, null);
	}

	public ClasspathResourceServlet(String folder, String resource) {
		this.folder = folder != null ? folder : "";
		this.resource = resource;
	}

	@Override
	protected String getResource(String uri) throws IOException {
		if (this.resource != null) {
			return resource;
		} else if (uri.startsWith(folder)) {
			return uri;
		} else {
			throw new IOException("Cannot find resource '" + uri
					+ "' within folder '" + this.folder + "'");
		}
	}

	@Override
	protected InputStream getResourceStream(String resource) throws IOException {
		if (this.resource != null && !this.resource.equals(resource)) {
			throw new IOException("Invalid resource: " + resource
					+ ". Expecting: " + this.resource);
		} else if (this.resource == null && !resource.startsWith(folder)) {
			throw new IOException("Cannot find resource '" + resource
					+ "' within folder '" + this.folder + "'");
		}

		InputStream stream = Path.root.file(resource).getResourceAsStream();
		if (stream != null) {
			return stream;
		} else {
			throw new IOException("Cannot find resource: " + resource);
		}
	}
}
