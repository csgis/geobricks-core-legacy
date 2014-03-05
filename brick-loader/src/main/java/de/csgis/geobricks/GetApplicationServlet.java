package de.csgis.geobricks;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.Application_;

@Singleton
public class GetApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appName = req.getAttribute(Geobricks.APPNAME_HTTP_ATTRIBUTE)
				.toString();

		try {
			getApplication(appName);
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
			em.remove(getApplication(appName));
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

	private Application getApplication(String appName) throws NoResultException {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Application> criteria = criteriaBuilder
				.createQuery(Application.class);
		Root<Application> root = criteria.from(Application.class);
		Predicate predicate = criteriaBuilder.equal(root.get(Application_.id),
				appName);
		criteria.where(predicate);

		TypedQuery<Application> query = em.createQuery(criteria);

		return query.getSingleResult();
	}
}
