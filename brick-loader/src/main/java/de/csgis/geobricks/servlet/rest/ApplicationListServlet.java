package de.csgis.geobricks.servlet.rest;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import de.csgis.geobricks.PersistenceUtils;
import de.csgis.geobricks.model.Application;

@Singleton
public class ApplicationListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private PersistenceUtils utils;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		JSONArray array = new JSONArray();

		List<Application> apps = utils.getApplicationList();
		for (Application app : apps) {
			array.add(app.getId());
		}

		response.setContentType("application/javascript");
		response.setCharacterEncoding("utf8");
		response.getWriter().write(array.toString());
	}
}
