package de.csgis.geobricks.servlet.rest;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PersistenceUtils;
import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.servlet.HTTPCodeServletException;

@Singleton
public class ApplicationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Inject
	private PersistenceUtils utils;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String appId = request.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();
		Application app = utils.getApplication(appId);
		if (app == null) {
			throw new HTTPCodeServletException("Application not found: "
					+ appId, 404);

		} else {
			JSONObject json = new JSONObject();
			json.element("id", appId);
			response.getWriter().write(json.toString());
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appId = req.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();
		Application app = utils.getApplication(appId);

		if (app == null) {
			throw new HTTPCodeServletException("Application not found: "
					+ appId, HttpServletResponse.SC_NOT_FOUND);
		} else {
			em.getTransaction().begin();
			try {
				em.remove(app);
				em.getTransaction().commit();
				throw new HTTPCodeServletException(204);
			} catch (NoResultException e) {
				em.getTransaction().rollback();
				throw new HTTPCodeServletException("Application not found: "
						+ appId, 404);
			}
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appId = req.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();
		Application app = utils.getApplication(appId);

		if (app == null) {
			app = new Application();
			app.setId(appId);

			em.getTransaction().begin();
			em.persist(app);
			em.getTransaction().commit();
		}

		throw new HTTPCodeServletException(204);
	}
}
