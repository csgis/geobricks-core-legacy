package de.csgis.geobricks;

import java.io.IOException;

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
		String path = req.getPathInfo();
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		String appName = path.substring(path.lastIndexOf('/'));

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Application> criteria = criteriaBuilder
				.createQuery(Application.class);
		Root<Application> root = criteria.from(Application.class);
		Predicate predicate = criteriaBuilder.equal(root.get(Application_.id),
				appName);
		criteria.where(predicate);

		TypedQuery<Application> query = em.createQuery(criteria);
		try {
			query.getSingleResult();
			resp.getOutputStream().write(
					("Application for " + appName).getBytes());
		} catch (NoResultException e) {
			throw new HTTPCodeServletException("Application not found: "
					+ appName, 404);
		}
	}
}
