package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class StaticTextServlet extends AbstractStaticServlet {
	private static final long serialVersionUID = 1L;

	public StaticTextServlet(String folder) {
		super(folder);
	}

	public StaticTextServlet(String folder, String resource) {
		super(folder, resource);
	}

	@Override
	protected void write(InputStream stream, HttpServletResponse response)
			throws IOException {
		IOUtils.copy(stream, response.getWriter());
	}
}
