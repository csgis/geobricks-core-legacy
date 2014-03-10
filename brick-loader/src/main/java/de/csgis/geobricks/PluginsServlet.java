package de.csgis.geobricks;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appName = req.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();
		String pluginName = req.getAttribute(
				Geobricks.PLUGIN_NAME_HTTP_ATTRIBUTE).toString();

		Application app = utils.getApplication(appName);
		app.getPlugins().add(new Plugin(pluginName));

		em.getTransaction().begin();
		em.merge(app);
		em.getTransaction().commit();

		throw new HTTPCodeServletException(204);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appName = req.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();
		String pluginName = req.getAttribute(
				Geobricks.PLUGIN_NAME_HTTP_ATTRIBUTE).toString();

		Plugin plugin = utils.getPlugin(pluginName);
		Application app = utils.getApplication(appName);

		app.getPlugins().remove(plugin);

		em.getTransaction().begin();
		em.merge(app);
		em.getTransaction().commit();
		throw new HTTPCodeServletException(204);
	}
}
