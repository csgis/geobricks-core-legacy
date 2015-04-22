package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

public class ConfigServletTest {
	private static final String PLUGIN_ID = "myplugin";
	private ConfigServlet servlet;

	@Before
	public void setup() {
		servlet = new ConfigServlet();
	}

	@Test
	public void modules() throws Exception {
		String[] modules = new String[] { "a", "b" };

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletContext context = context(
				JSONObject.fromObject("{'" + PLUGIN_ID + "' : {}}"), modules);
		when(context.getAttribute(Geobricks.ATTR_CONF_DIR)).thenReturn(
				"non_existing_dir");

		String config = servlet.getConfig(request, response, context);
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
		ServletContext context = context(
				JSONObject.fromObject("{'" + PLUGIN_ID
						+ "' : { a : {enabled : true}}}"), modules);
		when(context.getAttribute(Geobricks.ATTR_CONF_DIR)).thenReturn(
				"non_existing_dir");

		String config = servlet.getConfig(request, response, context);
		JSONObject json = JSONObject.fromObject(config);
		JSONObject moduleConfig = json.getJSONObject("config").getJSONObject(
				modules[0]);
		assertTrue(moduleConfig.getBoolean("enabled"));
	}

	@Test
	public void noModules() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletContext context = context(
				JSONObject.fromObject("{'" + PLUGIN_ID + "' : {}}"),
				new String[0]);
		when(context.getAttribute(Geobricks.ATTR_CONF_DIR)).thenReturn(
				"non_existing_dir");

		String config = servlet.getConfig(request, response, context);
		JSONObject json = JSONObject.fromObject(config);

		JSONArray configModules = json.getJSONObject("config").getJSONArray(
				"load-modules");
		assertEquals(0, configModules.size());
	}

	private ServletContext context(JSONObject pluginsConf, String[] modules) {
		ServletContext context = mock(ServletContext.class);
		when(context.getAttribute(eq(Geobricks.ATTR_PLUGINS_CONF))).thenReturn(
				pluginsConf);

		PluginDescriptor descriptor = new PluginDescriptor();
		Collections.addAll(descriptor.getModules(), modules);

		when(context.getAttribute(eq(Geobricks.ATTR_PLUGINS_DESC))).thenReturn(
				new PluginDescriptor[] { descriptor });

		return context;
	}
}
