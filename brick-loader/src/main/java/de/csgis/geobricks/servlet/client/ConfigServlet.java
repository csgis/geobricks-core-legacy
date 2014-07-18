package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.ConfiguredApplication;
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
		JSONObject pluginsConfiguration = (JSONObject) getServletContext()
				.getAttribute(ConfiguredApplication.ATTR_PLUGINS_CONF);

		List<String> modules = new ArrayList<String>();
		for (Object plugin : pluginsConfiguration.keySet()) {
			PluginDescriptor descriptor = pluginRegistry.getPlugin(plugin
					.toString());
			String[] pluginModules = descriptor.getModules();
			if (pluginModules != null) {
				Collections.addAll(modules, pluginModules);
			}
		}

		JSONObject moduleConfig = new JSONObject();
		for (Object plugin : pluginsConfiguration.keySet()) {
			JSONObject pluginConfiguration = pluginsConfiguration
					.getJSONObject(plugin.toString());
			Iterator<?> iterator = pluginConfiguration.keys();
			while (iterator.hasNext()) {
				String propertyName = (String) iterator.next();
				moduleConfig.element(propertyName,
						pluginConfiguration.get(propertyName));
			}
		}

		moduleConfig.element("main",
				modules.toArray(new String[modules.size()]));

		String json = new JSONObject().element("config", moduleConfig)
				.toString();

		resp.setContentType("application/javascript");
		resp.setCharacterEncoding("utf8");
		PrintWriter writer = resp.getWriter();
		writer.write("var require = " + json);
	}
}
