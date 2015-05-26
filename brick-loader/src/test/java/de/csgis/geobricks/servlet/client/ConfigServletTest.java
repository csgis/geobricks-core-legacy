package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.servlet.Config;

public class ConfigServletTest {
	private static final String PLUGIN_ID = "myplugin";
	private ConfigServlet servlet;

	@Before
	public void setup() throws ServletException {
		servlet = new ConfigServlet();
	}

	@Test
	public void modules() throws Exception {
		String[] modules = new String[] { "a", "b" };

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletConfig servletConfig = config(
				JSONObject.fromObject("{'" + PLUGIN_ID + "' : {}}"), modules);
		servlet.init(servletConfig);

		String config = servlet.getConfig(request, response,
				servletConfig.getServletContext());
		JSONObject json = JSONObject.fromObject(config);

		JSONArray configModules = json.getJSONObject("config").getJSONArray(
				"load-modules");
		assertEquals(modules.length, configModules.size());
		for (int i = 0; i < modules.length; i++) {
			assertTrue(configModules.contains(modules[i]));
		}
	}

	@Test
	public void moduleConfig() throws Exception {
		String[] modules = new String[] { "a" };

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletConfig servletConfig = config(
				JSONObject.fromObject("{'" + PLUGIN_ID
						+ "' : { a : {enabled : true}}}"), modules);
		servlet.init(servletConfig);

		String config = servlet.getConfig(request, response,
				servletConfig.getServletContext());
		JSONObject json = JSONObject.fromObject(config);
		JSONObject moduleConfig = json.getJSONObject("config").getJSONObject(
				modules[0]);
		assertTrue(moduleConfig.getBoolean("enabled"));
	}

	@Test
	public void noModules() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletConfig servletConfig = config(
				JSONObject.fromObject("{'" + PLUGIN_ID + "' : {}}"),
				new String[0]);
		servlet.init(servletConfig);

		String config = servlet.getConfig(request, response,
				servletConfig.getServletContext());
		JSONObject json = JSONObject.fromObject(config);

		JSONArray configModules = json.getJSONObject("config").getJSONArray(
				"load-modules");
		assertEquals(0, configModules.size());
	}

	private ServletConfig config(JSONObject pluginsConf, String[] modules)
			throws IOException {
		Config config = mock(Config.class);
		when(
				config.getApplicationConf(any(HttpServletRequest.class),
						any(HttpServletResponse.class)))
				.thenReturn(pluginsConf);

		PluginDescriptor descriptor = new PluginDescriptor();
		Collections.addAll(descriptor.getModules(), modules);

		when(config.getPluginDescriptors(any(JSONObject.class))).thenReturn(
				new PluginDescriptor[] { descriptor });

		ServletContext context = mock(ServletContext.class);
		when(context.getAttribute(Geobricks.ATTR_CONFIG)).thenReturn(config);

		ServletConfig servletConfig = mock(ServletConfig.class);
		when(servletConfig.getServletContext()).thenReturn(context);

		return servletConfig;
	}
}
