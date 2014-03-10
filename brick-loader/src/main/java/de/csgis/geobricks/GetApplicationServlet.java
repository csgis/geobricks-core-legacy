package de.csgis.geobricks;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
		Object app = req.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE);
		if (app == null) {
			// Requesting the list of applications
			handleAppList(resp);
		} else {
			// Requesting a single application
			handleSingleApp(app.toString(), resp);

		}
	}

	private void handleAppList(HttpServletResponse response) throws IOException {
		JSONArray array = new JSONArray();

		List<Application> apps = utils.getApplicationList();
		for (Application app : apps) {
			array.add(app.getId());
		}

		response.setContentType("application/javascript");
		response.setCharacterEncoding("utf8");
		response.getWriter().write(array.toString());
	}

	private void handleSingleApp(String appId, HttpServletResponse response)
			throws HTTPCodeServletException, IOException {
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
		Object appAttribute = req
				.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE);
		if (appAttribute == null) {
			// TODO This response should include an Allow header containing a
			// list of valid methods.
			throw new HTTPCodeServletException(
					"DELETE method requires an application id", 405);
		}

		String appId = appAttribute.toString();

		Application app = utils.getApplication(appId);
		if (app == null) {
			throw new HTTPCodeServletException("Application not found: "
					+ appId, 404);
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
		Object appAttribute = req
				.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE);
		if (appAttribute == null) {
			// TODO This response should include an Allow header containing a
			// list of valid methods.
			throw new HTTPCodeServletException(
					"PUT method requires an application id", 405);
		}

		String appId = appAttribute.toString();

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
