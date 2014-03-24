package de.csgis.geobricks.servlet.rest;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;

@Singleton
public class AvailablePluginsListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private PluginRegistry pluginRegistry;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		List<PluginDescriptor> plugins = pluginRegistry.getPlugins();
		JSONArray array = new JSONArray();
		for (PluginDescriptor plugin : plugins) {
			array.add(plugin.getName());
		}
		resp.setContentType("application/javascript");
		resp.setCharacterEncoding("utf8");
		resp.getWriter().write(array.toString());
	}
}
