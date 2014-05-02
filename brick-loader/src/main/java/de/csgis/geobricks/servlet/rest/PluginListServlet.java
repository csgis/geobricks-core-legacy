package de.csgis.geobricks.servlet.rest;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PersistenceUtils;
import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.ApplicationPluginUsage;
import de.csgis.geobricks.servlet.HTTPCodeServletException;

@Singleton
public class PluginListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
					+ appId, HttpServletResponse.SC_NOT_FOUND);
		}

		JSONArray array = new JSONArray();

		Set<ApplicationPluginUsage> plugins = app.getPlugins();
		for (ApplicationPluginUsage plugin : plugins) {
			array.add(plugin.getPluginId());
		}

		response.setContentType("application/javascript");
		response.setCharacterEncoding("utf8");
		response.getWriter().write(array.toString());
	}
}
