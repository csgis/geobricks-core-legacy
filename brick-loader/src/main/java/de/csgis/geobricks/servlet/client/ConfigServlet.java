package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.servlet.AbstractGeobricksServlet;

/**
 * Builds the json document that configures all the requirejs modules
 * 
 * @author fergonco
 */
@Singleton
public class ConfigServlet extends AbstractGeobricksServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String json = getConfig(req, resp);

		resp.setContentType("application/javascript");
		resp.setCharacterEncoding("utf8");
		PrintWriter writer = resp.getWriter();
		writer.write("var require = " + json);
	}

	public String getConfig(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		JSONObject ret = new JSONObject();
		Set<String> modules = new HashSet<String>();

		JSONObject gbappConf = getConfig()
				.getApplicationConf(request, response);

		PluginDescriptor[] descriptors = getConfig().getPluginDescriptors(
				gbappConf);
		for (PluginDescriptor descriptor : descriptors) {
			modules.addAll(descriptor.getModules());
		}

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
