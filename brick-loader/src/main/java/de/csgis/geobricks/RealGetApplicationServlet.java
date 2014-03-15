package de.csgis.geobricks;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

@Singleton
public class RealGetApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private PersistenceUtils utils;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appName = req.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();

		try {
			utils.getApplication(appName);
		} catch (NoResultException e) {
			throw new HTTPCodeServletException("Application not found: "
					+ appName, 404);
		}

		InputStream stream = this.getClass().getResourceAsStream("index.html");
		try {
			IOUtils.copy(stream, resp.getOutputStream());
		} finally {
			stream.close();
		}
	}

}
