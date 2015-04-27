package de.csgis.geobricks.servlet.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExternalResourceServlet extends AbstractStaticServlet {
	private File folder;

	public ExternalResourceServlet(File folder) {
		this.folder = folder;
	}

	@Override
	protected String getResource(String uri) throws IOException {
		if (!uri.contains("/")) {
			throw new IOException("Invalid URI: " + uri);
		} else {
			// We remove the first directory from the URI since it matches the
			// folder sent to the constructor
			String resource = uri.substring(uri.indexOf('/'));
			return new File(folder, resource).getAbsolutePath();
		}
	}

	@Override
	protected InputStream getResourceStream(String resource) throws IOException {
		return new FileInputStream(resource);
	}
}
