package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import de.csgis.geobricks.servlet.HTTPCodeServletException;

public abstract class AbstractStaticServlet extends HttpServlet {

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
			stream = getResourceStream(resource);
		} catch (IOException e) {
			throw new HTTPCodeServletException(e,
					HttpServletResponse.SC_NOT_FOUND);
		}

		try {
			if (isText(resource)) {
				IOUtils.copy(stream, resp.getWriter());
			} else {
				IOUtils.copy(stream, resp.getOutputStream());
			}
		} finally {
			stream.close();
		}
	}

	public boolean isText(String uri) {
		String s = uri.toLowerCase();
		return s.endsWith(".js") || s.endsWith(".css") || s.endsWith(".txt")
				|| s.endsWith(".html") || s.endsWith(".htm");
	}

	public void setContentTypeAndEncoding(HttpServletResponse resp, String uri) {
		if (uri.endsWith(".js")) {
			resp.setContentType("application/javascript");
			resp.setCharacterEncoding("UTF-8");
		} else if (uri.endsWith(".html")) {
			resp.setContentType("text/html");
			resp.setCharacterEncoding("UTF-8");
		} else if (uri.endsWith(".css")) {
			resp.setContentType("text/css");
			resp.setCharacterEncoding("UTF-8");
		}
	}

	protected abstract String getResource(String uri) throws IOException;

	protected abstract InputStream getResourceStream(String resource)
			throws IOException;
}
