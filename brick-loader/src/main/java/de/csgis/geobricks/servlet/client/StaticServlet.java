package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import de.csgis.geobricks.Geobricks;

/**
 * Serves static content from the resources space, starting to look in its own
 * package and in the folder specified by the concrete implementation
 * 
 * @author fergonco
 */
public abstract class StaticServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String folder;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		folder = getFolder();
	}

	protected abstract String getFolder();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String requestURI = req.getRequestURI();
		String resource = requestURI.substring(requestURI.indexOf(folder)
				+ folder.length() + 1);

		InputStream resourceStream = Geobricks.root.file(folder).file(resource)
				.getResourceAsStream();
		try {
			IOUtils.copy(resourceStream, resp.getOutputStream());
		} finally {
			resourceStream.close();
		}
	}

}
