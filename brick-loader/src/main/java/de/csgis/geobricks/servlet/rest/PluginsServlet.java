package de.csgis.geobricks.servlet.rest;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PersistenceUtils;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.ApplicationPluginUsage;
import de.csgis.geobricks.servlet.HTTPCodeServletException;

@Singleton
public class PluginsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Inject
	private PersistenceUtils utils;

	@Inject
	private PluginRegistry registry;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Application app = getApplication(request);

		String pluginId = request.getAttribute(
				Geobricks.PLUGIN_NAME_HTTP_ATTRIBUTE).toString();

		ApplicationPluginUsage plugin = app.getPlugin(pluginId);
		if (plugin == null) {
			throw new HTTPCodeServletException("Cannot find plugin '"
					+ pluginId + "' for application '" + app.getId() + "'",
					HttpServletResponse.SC_NOT_FOUND);
		}

		JSON json = JSONSerializer.toJSON(plugin.getConfiguration());

		response.setContentType("application/json");
		response.setCharacterEncoding("utf8");
		response.getWriter().write(json.toString(3));
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Application app = getApplication(req);
		String pluginId = req
				.getAttribute(Geobricks.PLUGIN_NAME_HTTP_ATTRIBUTE).toString();

		PluginDescriptor pluginDescriptor = registry.getPlugin(pluginId);
		if (pluginDescriptor == null) {
			throw new HTTPCodeServletException("Plugin does not exist: "
					+ pluginId, HttpServletResponse.SC_NOT_FOUND);
		}

		ApplicationPluginUsage plugin = new ApplicationPluginUsage(pluginId,
				app);
		String configurationString = IOUtils.toString(req.getInputStream());
		if (configurationString.length() > 0) {
			try {
				JSONSerializer.toJSON(configurationString);
			} catch (JSONException e) {
				throw new HTTPCodeServletException(
						HttpServletResponse.SC_BAD_REQUEST);
			}
			plugin.setConfiguration(configurationString);
		} else {
			plugin.setConfiguration(pluginDescriptor.getDefaultConfiguration());
		}

		app.putPlugin(plugin);

		em.getTransaction().begin();
		em.merge(plugin);
		em.merge(app);
		em.getTransaction().commit();

		throw new HTTPCodeServletException(HttpServletResponse.SC_NO_CONTENT);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Application app = getApplication(req);
		String pluginId = req
				.getAttribute(Geobricks.PLUGIN_NAME_HTTP_ATTRIBUTE).toString();

		ApplicationPluginUsage plugin = app.getPlugin(pluginId);

		if (plugin == null) {
			throw new HTTPCodeServletException("Cannot find plugin '"
					+ pluginId + "' for application '" + app.getId() + "'",
					HttpServletResponse.SC_NOT_FOUND);
		}

		app.getPlugins().remove(plugin);

		em.getTransaction().begin();
		em.merge(app);
		em.remove(plugin);
		em.getTransaction().commit();

		throw new HTTPCodeServletException(HttpServletResponse.SC_NO_CONTENT);
	}

	private Application getApplication(HttpServletRequest request)
			throws HTTPCodeServletException {
		String appName = request.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();
		Application app = utils.getApplication(appName);

		if (app == null) {
			throw new HTTPCodeServletException("Application not found: "
					+ appName, HttpServletResponse.SC_NOT_FOUND);
		}

		return app;
	}
}
