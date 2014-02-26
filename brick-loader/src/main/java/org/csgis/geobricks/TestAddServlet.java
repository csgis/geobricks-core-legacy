package org.csgis.geobricks;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csgis.geobricks.model.Installation;

@Singleton
public class TestAddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String id = req.getParameter("id");
		if (id == null || id.length() == 0) {
			id = "id-" + System.currentTimeMillis();
		}
		Installation installation = new Installation();
		installation.setId(id);

		em.getTransaction().begin();
		em.persist(installation);
		em.getTransaction().commit();

		resp.getWriter().write("Installation successfully added: " + id);
	}
}
