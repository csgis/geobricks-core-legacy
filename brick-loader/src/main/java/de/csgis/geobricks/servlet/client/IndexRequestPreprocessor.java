package de.csgis.geobricks.servlet.client;

import java.io.IOException;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PersistenceUtils;
import de.csgis.geobricks.servlet.HTTPCodeServletException;
import de.csgis.geobricks.servlet.client.StaticServlet.RequestPreprocessor;

public class IndexRequestPreprocessor implements RequestPreprocessor {
	@Inject
	private PersistenceUtils utils;

	@Override
	public void preprocess(HttpServletRequest request) throws ServletException,
			IOException {
		String appName = request.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();

		try {
			utils.getApplication(appName);
		} catch (NoResultException e) {
			throw new HTTPCodeServletException("Application not found: "
					+ appName, HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
