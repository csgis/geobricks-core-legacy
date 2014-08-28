package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.csgis.geobricks.ConfiguredApplication;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.guice.RuntimeModule;

public class ConfigServletTest {
	private static final String PLUGIN_ID = "myplugin";
	private ConfigServlet servlet;
	private PluginRegistry pluginRegistry;

	@Before
	public void setup() {
		Injector injector = Guice.createInjector(new RuntimeModule());
		servlet = injector.getInstance(ConfigServlet.class);
		pluginRegistry = injector.getInstance(PluginRegistry.class);
	}

	@Test
	public void modules() {
		String[] modules = new String[] { "a", "b" };
		PluginDescriptor plugin = plugin(modules);
		pluginRegistry.putPlugin(plugin);

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletContext context = context(JSONObject.fromObject("{'" + PLUGIN_ID
				+ "' : {}}"));

		String config = servlet.getConfig(request, response, context);
		JSONObject json = JSONObject.fromObject(config);

		JSONArray configModules = json.getJSONObject("config").getJSONArray(
				"main");
		assertEquals(modules.length, configModules.size());
		for (int i = 0; i < modules.length; i++) {
			assertEquals(modules[i], configModules.getString(i));
		}
	}

	@Test
	public void moduleConfig() {
		String[] modules = new String[] { "a" };
		PluginDescriptor plugin = plugin(modules);
		pluginRegistry.putPlugin(plugin);

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletContext context = context(JSONObject.fromObject("{'" + PLUGIN_ID
				+ "' : { a : {enabled : true}}}"));

		String config = servlet.getConfig(request, response, context);
		JSONObject json = JSONObject.fromObject(config);
		JSONObject moduleConfig = json.getJSONObject("config").getJSONObject(
				modules[0]);
		assertTrue(moduleConfig.getBoolean("enabled"));
	}

	@Test
	public void noModules() {
		PluginDescriptor plugin = plugin(null);
		pluginRegistry.putPlugin(plugin);

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletContext context = context(JSONObject.fromObject("{'" + PLUGIN_ID
				+ "' : {}}"));

		String config = servlet.getConfig(request, response, context);
		JSONObject json = JSONObject.fromObject(config);

		JSONArray configModules = json.getJSONObject("config").getJSONArray(
				"main");
		assertEquals(0, configModules.size());
	}

	@Test
	public void customPluginConfig() {
		String[] modules = new String[] { "a", "b" };
		PluginDescriptor plugin = plugin(modules);
		pluginRegistry.putPlugin(plugin);

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletContext context = context(JSONObject.fromObject("{'" + PLUGIN_ID
				+ "' : {}}"));

		servlet.getConfig(request, response, context);

		verify(plugin).config(eq(request), eq(response), any(JSONObject.class),
				anyString());
	}

	private PluginDescriptor plugin(String[] modules) {
		PluginDescriptor plugin = mock(PluginDescriptor.class);
		when(plugin.getId()).thenReturn(PLUGIN_ID);
		when(plugin.getModules()).thenReturn(modules);

		return plugin;
	}

	private ServletContext context(JSONObject pluginsConf) {
		ServletContext context = mock(ServletContext.class);
		when(context.getAttribute(eq(ConfiguredApplication.ATTR_PLUGINS_CONF)))
				.thenReturn(pluginsConf);
		when(context.getAttribute(eq(Geobricks.CONF_DIR_ATTRIBUTE)))
				.thenReturn("");

		return context;

	}
}
