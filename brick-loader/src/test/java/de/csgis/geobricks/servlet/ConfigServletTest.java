package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.guice.GuiceServletConfig;
import de.csgis.geobricks.servlet.Config;
import de.csgis.geobricks.servlet.ConfigServlet;

public class ConfigServletTest {
	private static final String PLUGIN_ID = "myplugin";
	private ConfigServlet servlet;
	private Config config;

	@Before
	public void setup() throws ServletException {
		servlet = new ConfigServlet();
		config = mock(Config.class);
		GuiceServletConfig.setInjector(Guice
				.createInjector(new AbstractModule() {
					@Override
					protected void configure() {
						bind(Config.class).toInstance(config);
					}
				}));
	}

	@After
	public void tearDown() {
		GuiceServletConfig.setInjector(null);
	}

	@Test
	public void modules() throws Exception {
		String[] modules = new String[] { "a", "b" };

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletConfig servletConfig = config(
				JSONObject.fromObject("{'" + PLUGIN_ID + "' : {}}"), modules);
		servlet.init(servletConfig);

		String config = servlet.getConfig(request, response);
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

		String config = servlet.getConfig(request, response);
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

		String config = servlet.getConfig(request, response);
		JSONObject json = JSONObject.fromObject(config);

		JSONArray configModules = json.getJSONObject("config").getJSONArray(
				"load-modules");
		assertEquals(0, configModules.size());
	}

	private ServletConfig config(JSONObject pluginsConf, String[] modules)
			throws IOException {
		when(
				this.config.getApplicationConf(any(HttpServletRequest.class),
						any(HttpServletResponse.class)))
				.thenReturn(pluginsConf);

		PluginDescriptor descriptor = new PluginDescriptor();
		Collections.addAll(descriptor.getModules(), modules);

		when(this.config.getPluginDescriptors(any(JSONObject.class)))
				.thenReturn(new PluginDescriptor[] { descriptor });

		ServletContext context = mock(ServletContext.class);
		when(context.getResourceAsStream(anyString())).thenReturn(
				new ByteArrayInputStream("{}".getBytes()));

		ServletConfig servletConfig = mock(ServletConfig.class);
		when(servletConfig.getServletContext()).thenReturn(context);

		return servletConfig;
	}
}
