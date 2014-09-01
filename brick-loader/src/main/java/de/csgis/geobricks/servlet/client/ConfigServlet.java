package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.CustomConfigurator;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.config.ConfiguredApplication;

/**
 * Builds the json document that configures all the requirejs modules
 * 
 * @author fergonco
 */
@Singleton
public class ConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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

		JSONObject config = new JSONObject();
		Set<String> modules = new HashSet<String>();

		PluginDescriptor[] descriptors = (PluginDescriptor[]) context
				.getAttribute(Geobricks.DESCRIPTORS_ATTRIBUTE);
		for (PluginDescriptor descriptor : descriptors) {
			modules.addAll(descriptor.getModules());
		}

		for (Object plugin : pluginsConfiguration.keySet()) {
			// Add configuration for each module within plugin configuration
			JSONObject pluginConfiguration = pluginsConfiguration
					.getJSONObject(plugin.toString());
			for (Object key : pluginConfiguration.keySet()) {
				String module = key.toString();
				config.element(module, pluginConfiguration.get(module));
			}
		}

		config.element("main", modules);

		// Custom configuration
		CustomConfigurator[] configurators = (CustomConfigurator[]) context
				.getAttribute(Geobricks.CONFIGURATORS_ATTRIBUTE);
		String confDir = context.getAttribute(Geobricks.CONF_DIR_ATTRIBUTE)
				.toString();
		for (CustomConfigurator configurator : configurators) {
			configurator.config(request, response, config, confDir);
		}

		return new JSONObject().element("config", config).toString();
	}
}
