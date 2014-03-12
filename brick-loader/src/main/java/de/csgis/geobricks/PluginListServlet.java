package de.csgis.geobricks;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

@Singleton
public class PluginListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Generate plugin list dynamically
		JSONArray array = new JSONArray();
		array.add("hello");

		resp.setContentType("application/javascript");
		resp.setCharacterEncoding("utf8");
		resp.getWriter().write(array.toString());
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		throw new HTTPCodeServletException(405);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		throw new HTTPCodeServletException(405);
	}
}
