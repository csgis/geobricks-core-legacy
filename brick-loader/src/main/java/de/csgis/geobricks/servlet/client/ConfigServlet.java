package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.ConfiguredApplication;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;

/**
 * Builds the json document that configures all the requirejs modules
 * 
 * @author fergonco
 */
@Singleton
public class ConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private PluginRegistry pluginRegistry;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String json = getConfig(req, resp, getServletContext());

		resp.setContentType("application/javascript");
		resp.setCharacterEncoding("utf8");
		PrintWriter writer = resp.getWriter();
		writer.write("var require = " + json);
	}

	public String getConfig(HttpServletRequest request,
			HttpServletResponse response, ServletContext context) {
		JSONObject pluginsConfiguration = (JSONObject) context
				.getAttribute(ConfiguredApplication.ATTR_PLUGINS_CONF);

		JSONObject moduleConfig = new JSONObject();
		List<String> modules = new ArrayList<String>();

		for (Object plugin : pluginsConfiguration.keySet()) {
			PluginDescriptor descriptor = pluginRegistry.getPlugin(plugin
					.toString());

			// Add plugin modules
			String[] pluginModules = descriptor.getModules();
			if (pluginModules != null) {
				Collections.addAll(modules, pluginModules);
			}

			// Add configuration for each module within plugin configuration
			JSONObject pluginConfiguration = pluginsConfiguration
					.getJSONObject(plugin.toString());
			for (Object key : pluginConfiguration.keySet()) {
				String module = key.toString();
				moduleConfig.element(module, pluginConfiguration.get(module));
			}
		}

		// Manage custom configuration for each plugin
		String confDir = context.getAttribute(Geobricks.CONF_DIR_ATTRIBUTE)
				.toString();
		for (Object plugin : pluginsConfiguration.keySet()) {
			PluginDescriptor descriptor = pluginRegistry.getPlugin(plugin
					.toString());
			descriptor.config(request, response, moduleConfig, confDir);
		}

		moduleConfig.element("main",
				modules.toArray(new String[modules.size()]));

		return new JSONObject().element("config", moduleConfig).toString();
	}
}
