package de.csgis.geobricks.servlet.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import de.csgis.geobricks.ClientModuleConfiguration;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PersistenceUtils;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.Plugin;
import de.csgis.geobricks.servlet.HTTPCodeServletException;

@Singleton
public class PluginConfigurationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
		Plugin plugin = utils.getPlugin(pluginId);

		if (plugin == null || !app.getPlugins().contains(plugin)) {
			throw new HTTPCodeServletException("Cannot find plugin '"
					+ pluginId + "' for application '" + app.getId() + "'",
					HttpServletResponse.SC_NOT_FOUND);
		}

		JSONObject json = new JSONObject();
		json.element("id", pluginId);
		PluginDescriptor descriptor = registry.getPlugin(pluginId);
		ClientModuleConfiguration[] configurations = descriptor
				.getClientModuleConfiguration();
		for (ClientModuleConfiguration clientModuleConfiguration : configurations) {
			json.element(clientModuleConfiguration.getModuleName(),
					JSONSerializer.toJSON(clientModuleConfiguration
							.getJsonContent()));
		}

		response.setContentType("application/json");
		response.setCharacterEncoding("utf8");
		response.getWriter().write(json.toString());
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Application app = getApplication(req);
		String pluginId = req
				.getAttribute(Geobricks.PLUGIN_NAME_HTTP_ATTRIBUTE).toString();

		PluginDescriptor plugin = registry.getPlugin(pluginId);
		if (plugin == null) {
			throw new HTTPCodeServletException("Plugin does not exist: "
					+ pluginId, HttpServletResponse.SC_NOT_FOUND);
		}
		PluginDescriptor descriptor = registry.getPlugin(pluginId);
		String configuration = req.getParameter("conf");
		JSONObject jsonConfiguration;
		try {
			jsonConfiguration = (JSONObject) JSONSerializer
					.toJSON(configuration);
		} catch (ClassCastException e) {
			throw new HTTPCodeServletException(
					"The configuration must be an element", 400);
		}
		ArrayList<ClientModuleConfiguration> clientConfiguration = new ArrayList<>();
		@SuppressWarnings("rawtypes")
		Iterator iterator = jsonConfiguration.keys();
		while (iterator.hasNext()) {
			String moduleName = (String) iterator.next();
			JSON moduleConfiguration = (JSON) jsonConfiguration.get(moduleName);
			clientConfiguration.add(new ClientModuleConfiguration(moduleName,
					moduleConfiguration.toString(2)));
		}
		ClientModuleConfiguration[] clientConfigurationArray = clientConfiguration
				.toArray(new ClientModuleConfiguration[clientConfiguration
						.size()]);
		descriptor.setClientModuleConfiguration(clientConfigurationArray);

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
