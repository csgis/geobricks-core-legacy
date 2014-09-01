package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.CustomConfigurator;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.config.ConfiguredApplication;

public class ConfigServletTest {
	private static final String PLUGIN_ID = "myplugin";
	private ConfigServlet servlet;

	@Before
	public void setup() {
		servlet = new ConfigServlet();
	}

	@Test
	public void modules() {
		String[] modules = new String[] { "a", "b" };

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletContext context = context(
				JSONObject.fromObject("{'" + PLUGIN_ID + "' : {}}"), modules);

		String config = servlet.getConfig(request, response, context);
		JSONObject json = JSONObject.fromObject(config);

		JSONArray configModules = json.getJSONObject("config").getJSONArray(
				"main");
		assertEquals(modules.length, configModules.size());
		for (int i = 0; i < modules.length; i++) {
			assertTrue(configModules.contains(modules[i]));
		}
	}

	@Test
	public void moduleConfig() {
		String[] modules = new String[] { "a" };

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletContext context = context(
				JSONObject.fromObject("{'" + PLUGIN_ID
						+ "' : { a : {enabled : true}}}"), modules);

		String config = servlet.getConfig(request, response, context);
		JSONObject json = JSONObject.fromObject(config);
		JSONObject moduleConfig = json.getJSONObject("config").getJSONObject(
				modules[0]);
		assertTrue(moduleConfig.getBoolean("enabled"));
	}

	@Test
	public void noModules() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletContext context = context(
				JSONObject.fromObject("{'" + PLUGIN_ID + "' : {}}"),
				new String[0]);

		String config = servlet.getConfig(request, response, context);
		JSONObject json = JSONObject.fromObject(config);

		JSONArray configModules = json.getJSONObject("config").getJSONArray(
				"main");
		assertEquals(0, configModules.size());
	}

	@Test
	public void customConfigurator() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		CustomConfigurator configurator = mock(CustomConfigurator.class);

		ServletContext context = context(
				JSONObject.fromObject("{'" + PLUGIN_ID + "' : {}}"),
				new String[0]);
		when(context.getAttribute(eq(Geobricks.CONFIGURATORS_ATTRIBUTE)))
				.thenReturn(new CustomConfigurator[] { configurator });

		servlet.getConfig(request, response, context);

		verify(configurator).config(eq(request), eq(response),
				any(JSONObject.class), anyString());
	}

	private ServletContext context(JSONObject pluginsConf, String[] modules) {
		ServletContext context = mock(ServletContext.class);
		when(context.getAttribute(eq(ConfiguredApplication.ATTR_PLUGINS_CONF)))
				.thenReturn(pluginsConf);
		when(context.getAttribute(eq(Geobricks.CONF_DIR_ATTRIBUTE)))
				.thenReturn("");

		PluginDescriptor descriptor = new PluginDescriptor();
		Collections.addAll(descriptor.getModules(), modules);

		when(context.getAttribute(eq(Geobricks.CONFIGURATORS_ATTRIBUTE)))
				.thenReturn(new CustomConfigurator[0]);
		when(context.getAttribute(eq(Geobricks.DESCRIPTORS_ATTRIBUTE)))
				.thenReturn(new PluginDescriptor[] { descriptor });

		return context;
	}
}
