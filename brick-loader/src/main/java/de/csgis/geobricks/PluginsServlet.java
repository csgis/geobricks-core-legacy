package de.csgis.geobricks;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.Plugin;

@Singleton
public class PluginsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Inject
	private PersistenceUtils utils;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Application app = getApplication(req);

		Object pluginAttribute = req
				.getAttribute(Geobricks.PLUGIN_NAME_HTTP_ATTRIBUTE);
		if (pluginAttribute != null) {
			String pluginId = pluginAttribute.toString();
			Plugin plugin = utils.getPlugin(pluginId);
			if (plugin == null || !app.getPlugins().contains(plugin)) {
				throw new HTTPCodeServletException("Cannot find plugin '"
						+ pluginId + "' for application '" + app.getId() + "'",
						404);
			}

			JSONObject json = new JSONObject();
			json.element("id", plugin.getId());

			resp.setContentType("application/javascript");
			resp.setCharacterEncoding("utf8");
			resp.getWriter().write(json.toString());
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Application app = getApplication(req);
		String pluginId = getPluginId(req);

		app.getPlugins().add(new Plugin(pluginId));

		em.getTransaction().begin();
		em.merge(app);
		em.getTransaction().commit();

		throw new HTTPCodeServletException(204);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Application app = getApplication(req);
		String pluginId = getPluginId(req);

		Plugin plugin = utils.getPlugin(pluginId);

		if (plugin == null) {
			throw new HTTPCodeServletException("Cannot find plugin '"
					+ pluginId + "' for application '" + app.getId() + "'", 404);
		}

		app.getPlugins().remove(plugin);

		em.getTransaction().begin();
		em.merge(app);
		em.getTransaction().commit();

		throw new HTTPCodeServletException(204);
	}

	private Application getApplication(HttpServletRequest request)
			throws HTTPCodeServletException {
		String appName = request.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();
		Application app = utils.getApplication(appName);

		if (app == null) {
			throw new HTTPCodeServletException("Application not found: "
					+ appName, 404);
		}

		return app;
	}

	private String getPluginId(HttpServletRequest request)
			throws HTTPCodeServletException {
		Object pluginAttribute = request
				.getAttribute(Geobricks.PLUGIN_NAME_HTTP_ATTRIBUTE);
		if (pluginAttribute == null) {
			throw new HTTPCodeServletException(
					"DELETE method requires a plugin id", 405);
		}
		return pluginAttribute.toString();
	}
}
