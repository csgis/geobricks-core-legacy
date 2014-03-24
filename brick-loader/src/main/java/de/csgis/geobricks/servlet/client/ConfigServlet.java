package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.ClientModuleConfiguration;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PersistenceUtils;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;
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

	@Inject
	private PluginRegistry pluginRegistry;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String appName = req.getAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE)
				.toString();
		Application app = utils.getApplication(appName);

		Set<Plugin> plugins = app.getPlugins();
		List<String> modules = new ArrayList<String>();
		for (Plugin plugin : plugins) {
			PluginDescriptor descriptor = pluginRegistry.getPlugin(plugin
					.getId());
			Collections.addAll(modules, descriptor.getModules());
		}

		JSONObject moduleConfig = new JSONObject();
		moduleConfig.element("main",
				modules.toArray(new String[modules.size()]));
		ClientModuleConfiguration[] configuration = pluginRegistry
				.getClientModuleConfiguration();
		for (ClientModuleConfiguration clientModuleConfiguration : configuration) {
			moduleConfig.element(clientModuleConfiguration.getModuleName(),
					clientModuleConfiguration.getEntity());
		}

		String json = new JSONObject().element("config", moduleConfig)
				.toString();

		resp.setContentType("application/javascript");
		resp.setCharacterEncoding("utf8");
		PrintWriter writer = resp.getWriter();
		writer.write("var require = " + json);
	}
}
