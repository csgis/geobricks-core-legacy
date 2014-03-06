package de.csgis.geobricks;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import de.csgis.geobricks.model.Application;

@Singleton
public class GetApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Inject
	private PersistenceUtils utils;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appName = req.getAttribute(Geobricks.APPNAME_HTTP_ATTRIBUTE)
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

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appName = req.getAttribute(Geobricks.APPNAME_HTTP_ATTRIBUTE)
				.toString();

		em.getTransaction().begin();
		try {
			em.remove(utils.getApplication(appName));
			em.getTransaction().commit();
			throw new HTTPCodeServletException(204);
		} catch (NoResultException e) {
			em.getTransaction().rollback();
			throw new HTTPCodeServletException("Application not found: "
					+ appName, 404);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appName = req.getAttribute(Geobricks.APPNAME_HTTP_ATTRIBUTE)
				.toString();

		Application app = new Application();
		app.setId(appName);

		em.getTransaction().begin();
		em.persist(app);
		em.getTransaction().commit();

		throw new HTTPCodeServletException(204);
	}
}
