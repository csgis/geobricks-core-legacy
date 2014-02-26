package org.csgis.geobricks;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csgis.geobricks.model.Installation;

@Singleton
public class TestGetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Query query = em.createQuery("SELECT i FROM "
				+ Installation.class.getName() + " i");
		List<?> list = query.getResultList();
		for (Object object : list) {
			Installation i = (Installation) object;
			resp.getWriter().write(i.getId() + "\n");
		}
	}
}
