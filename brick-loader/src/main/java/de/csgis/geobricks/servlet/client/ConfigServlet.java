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
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.servlet.Config;

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
			HttpServletResponse response, ServletContext context)
			throws IOException {
		Config config = (Config) context.getAttribute(Geobricks.ATTR_CONFIG);

		JSONObject ret = new JSONObject();
		Set<String> modules = new HashSet<String>();

		PluginDescriptor[] descriptors = config.getPluginDescriptors();
		for (PluginDescriptor descriptor : descriptors) {
			modules.addAll(descriptor.getModules());
		}

		JSONObject gbappConf = config.getApplicationConf();
		for (Object plugin : gbappConf.keySet()) {
			JSONObject pluginConf = gbappConf.getJSONObject(plugin.toString());
			// Add configuration for each module within plugin configuration
			for (Object key : pluginConf.keySet()) {
				String module = key.toString();
				ret.element(module, pluginConf.get(module));
			}
		}

		ret.element("load-modules", modules);
		return new JSONObject().element("config", ret).toString();
	}
}
