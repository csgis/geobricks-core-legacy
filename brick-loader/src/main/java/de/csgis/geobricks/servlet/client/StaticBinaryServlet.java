package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class StaticBinaryServlet extends AbstractStaticServlet {
	private static final long serialVersionUID = 1L;

	public StaticBinaryServlet(String folder) {
		super(folder);
	}

	@Override
	protected void write(InputStream stream, HttpServletResponse response)
			throws IOException {
		IOUtils.copy(stream, response.getOutputStream());
	}
}
