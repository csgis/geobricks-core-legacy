package de.csgis.geobricks.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

public class ConfiguredApplicationTest {
	private static final String REAL_PATH = "";
	private static final String DEFAULT_CONFIG_PATH = REAL_PATH
			+ File.separator + "WEB-INF" + File.separator + "default_config";
	private static final String PLUGIN_ID = "myplugin";
	private static final String APP_NAME = "geobricks_testing_app_conf_dir";

	private ServletContext context;
	private ConfiguredApplication listener;
	private PluginDescriptor descriptor;

	@Before
	public void setup() {
		listener = new ConfiguredApplication();
		context = mock(ServletContext.class);

		descriptor = new PluginDescriptor();
		descriptor.setId(PLUGIN_ID);
		when(context.getAttribute(Geobricks.DESCRIPTORS_ATTRIBUTE)).thenReturn(
				new PluginDescriptor[] { descriptor });
		when(context.getRealPath(anyString())).thenReturn(REAL_PATH);
		when(context.getContextPath()).thenReturn(APP_NAME);
	}

	@Test
	public void customPluginConfig() throws Exception {
		String json = "{" + PLUGIN_ID + ": { mymodule : { enabled : true }}}";
		when(context.getResourceAsStream(anyString())).thenReturn(
				new ByteArrayInputStream(json.getBytes()));

		JSONObject conf = configure(ConfiguredApplication.ATTR_PLUGINS_CONF,
				JSONObject.class);
		assertTrue(conf.getJSONObject(PLUGIN_ID).getJSONObject("mymodule")
				.getBoolean("enabled"));
	}

	@Test
	public void defaultPluginConfigWhenEmpty() throws Exception {
		descriptor.setDefaultConfiguration(JSONObject
				.fromObject("{ mymodule : { enabled : false }}"));

		String json = "{" + PLUGIN_ID + ": {}}";
		when(context.getResourceAsStream(anyString())).thenReturn(
				new ByteArrayInputStream(json.getBytes()));

		JSONObject conf = configure(ConfiguredApplication.ATTR_PLUGINS_CONF,
				JSONObject.class);

		assertFalse(conf.getJSONObject(PLUGIN_ID).getJSONObject("mymodule")
				.getBoolean("enabled"));
	}

	@Test
	public void undefinedConfDir() throws Exception {
		String json = "{" + PLUGIN_ID + ": {}}";
		when(context.getResourceAsStream(anyString())).thenReturn(
				new ByteArrayInputStream(json.getBytes()));

		String conf = configure(Geobricks.CONF_DIR_ATTRIBUTE, String.class);
		assertEquals(DEFAULT_CONFIG_PATH, conf);
	}

	@Test
	public void nonExistingConfDirBase() throws Exception {
		String json = "{" + PLUGIN_ID + ": {}}";
		when(context.getResourceAsStream(anyString())).thenReturn(
				new ByteArrayInputStream(json.getBytes()));

		System.setProperty("GEOBRICKS_CONF_DIR", "non_existing");
		String conf = configure(Geobricks.CONF_DIR_ATTRIBUTE, String.class);
		assertEquals(DEFAULT_CONFIG_PATH, conf);
	}

	@Test
	public void nonExistingConfDirForApp() throws Exception {
		String json = "{" + PLUGIN_ID + ": {}}";
		when(context.getResourceAsStream(anyString())).thenReturn(
				new ByteArrayInputStream(json.getBytes()));

		System.setProperty("GEOBRICKS_CONF_DIR",
				System.getProperty("java.io.tmpdir"));
		String conf = configure(Geobricks.CONF_DIR_ATTRIBUTE, String.class);
		assertEquals(DEFAULT_CONFIG_PATH, conf);
	}

	@Test
	public void confDirFromVariable() throws Exception {
		String json = "{" + PLUGIN_ID + ": {}}";
		when(context.getResourceAsStream(anyString())).thenReturn(
				new ByteArrayInputStream(json.getBytes()));

		File dir = new File(System.getProperty("java.io.tmpdir"), APP_NAME);
		dir.mkdir();

		System.setProperty("GEOBRICKS_CONF_DIR", dir.getParent());
		String conf = configure(Geobricks.CONF_DIR_ATTRIBUTE, String.class);
		assertEquals(dir.getAbsolutePath(), conf);

		dir.delete();
	}

	@Test
	public void testGetConfiguredAppId() throws Exception {
		String configuredId = "configuredid";
		when(context.getInitParameter("geobricks-app-id")).thenReturn(
				configuredId);
		when(context.getContextPath()).thenReturn("/myapp");

		assertEquals(listener.getApplicationId(context), configuredId);
	}

	@Test
	public void testGetAppIdFromURL() throws Exception {
		String urlId = "myapp";
		when(context.getInitParameter("geobricks-app-id")).thenReturn(null);
		when(context.getContextPath()).thenReturn("/" + urlId);

		assertEquals(urlId, listener.getApplicationId(context));
	}

	@Test
	public void pluginDescriptorOrder() throws Exception {
		String id1 = "p1";
		String id2 = "p2";

		PluginDescriptor p1 = new PluginDescriptor();
		p1.setId(id1);
		PluginDescriptor p2 = new PluginDescriptor();
		p2.setId(id2);

		when(context.getAttribute(Geobricks.DESCRIPTORS_ATTRIBUTE)).thenReturn(
				new PluginDescriptor[] { p1, p2 });

		String json = "{" + id2 + ": {}, " + id1 + " :{}}";
		when(context.getResourceAsStream(anyString())).thenReturn(
				new ByteArrayInputStream(json.getBytes()));

		PluginDescriptor[] descriptors = configure(
				Geobricks.DESCRIPTORS_ATTRIBUTE, PluginDescriptor[].class);
		assertEquals(id2, descriptors[0].getId());
		assertEquals(id1, descriptors[1].getId());
	}

	private <T extends Object> T configure(String attribute, Class<T> clazz)
			throws Exception {
		listener.configureContext(context);
		ArgumentCaptor<T> captor = ArgumentCaptor.forClass(clazz);
		verify(context).setAttribute(eq(attribute), captor.capture());
		return captor.getValue();
	}
}
