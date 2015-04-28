package de.csgis.geobricks.servlet.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Servlet to serve static resources from a filesystem directory. It assumes the
 * first directory on the sent URIs correspond to the directory sent to the
 * constructor; this is, it simply ignores the beginning of the URIs until the
 * first '/'.
 * 
 * @author vicgonco
 */
public class ExternalResourceServlet extends AbstractStaticServlet {
	private File directory;

	public ExternalResourceServlet(File directory) {
		this.directory = directory;
	}

	@Override
	protected String getResource(String uri) throws IOException {
		if (!uri.contains("/")) {
			throw new IOException("Invalid URI: " + uri);
		} else {
			// We remove the first directory from the URI since it matches the
			// folder sent to the constructor
			String resource = uri.substring(uri.indexOf('/'));
			return new File(directory, resource).getAbsolutePath();
		}
	}

	@Override
	protected InputStream getResourceAsStream(String resource) throws IOException {
		return new FileInputStream(resource);
	}
}
