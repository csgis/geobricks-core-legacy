package de.csgis.geobricks;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.Plugin;

/**
 * Builds the json document that configures all the requirejs modules
 * 
 * @author fergonco
 */
@Singleton
public class ConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private PersistenceUtils utils;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appName = req.getAttribute(Geobricks.APPNAME_HTTP_ATTRIBUTE)
				.toString();
		Application app = utils.getApplication(appName);

		Set<Plugin> plugins = app.getPlugins();
		String[] pluginIds = new String[plugins.size()];
		Iterator<Plugin> iterator = plugins.iterator();
		for (int i = 0; i < pluginIds.length; i++) {
			pluginIds[i] = iterator.next().getId();
		}

		JSONObject moduleConfig = new JSONObject();
		moduleConfig.element("main", pluginIds);

		String json = new JSONObject().element("config", moduleConfig)
				.toString();

		resp.setContentType("application/javascript");
		resp.setCharacterEncoding("utf8");
		PrintWriter writer = resp.getWriter();
		writer.write("var require = " + json);
	}
}
