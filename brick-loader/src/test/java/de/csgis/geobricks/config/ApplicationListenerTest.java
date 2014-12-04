package de.csgis.geobricks.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

public class ApplicationListenerTest {
	private static final String REAL_PATH = "";
	private static final String DEFAULT_CONFIG_PATH = REAL_PATH
			+ File.separator + "WEB-INF" + File.separator + "default_config";
	private static final String PLUGIN_ID = "myplugin";
	private static final String APP_NAME = "geobricks_testing_app_conf_dir";

	private ServletContext context;
	private ApplicationListener listener;
	private PluginDescriptor descriptor;

	@Before
	public void setup() {
		listener = new ApplicationListener();
		context = mock(ServletContext.class);

		descriptor = new PluginDescriptor();
		descriptor.setId(PLUGIN_ID);
		when(context.getAttribute(Geobricks.ATTR_PLUGINS_DESC)).thenReturn(
				new PluginDescriptor[] { descriptor });
		when(context.getRealPath(anyString())).thenReturn(REAL_PATH);
		when(context.getContextPath()).thenReturn(APP_NAME);
	}

	@Test
	public void customPluginConfig() throws Exception {
		JSONObject json = JSONObject.fromObject("{" + PLUGIN_ID
				+ ": { mymodule : { enabled : true }}}");
		when(context.getAttribute(Geobricks.ATTR_PLUGINS_CONF))
				.thenReturn(json);

		JSONObject conf = configure(Geobricks.ATTR_PLUGINS_CONF,
				JSONObject.class);
		assertTrue(conf.getJSONObject(PLUGIN_ID).getJSONObject("mymodule")
				.getBoolean("enabled"));
	}

	@Test
	public void defaultPluginConfigWhenEmpty() throws Exception {
		descriptor.setDefaultConfiguration(JSONObject
				.fromObject("{ mymodule : { enabled : false }}"));

		JSONObject json = JSONObject.fromObject("{" + PLUGIN_ID + ": {}}");
		when(context.getAttribute(Geobricks.ATTR_PLUGINS_CONF))
				.thenReturn(json);

		JSONObject conf = configure(Geobricks.ATTR_PLUGINS_CONF,
				JSONObject.class);

		assertFalse(conf.getJSONObject(PLUGIN_ID).getJSONObject("mymodule")
				.getBoolean("enabled"));
	}

	@Test
	public void undefinedConfDir() throws Exception {
		JSONObject json = JSONObject.fromObject("{" + PLUGIN_ID + ": {}}");
		when(context.getAttribute(Geobricks.ATTR_PLUGINS_CONF))
				.thenReturn(json);

		String conf = configure(Geobricks.ATTR_CONF_DIR, String.class);
		assertEquals(DEFAULT_CONFIG_PATH, conf);
	}

	@Test
	public void nonExistingConfDirBase() throws Exception {
		JSONObject json = JSONObject.fromObject("{" + PLUGIN_ID + ": {}}");
		when(context.getAttribute(Geobricks.ATTR_PLUGINS_CONF))
				.thenReturn(json);

		System.setProperty("GEOBRICKS_CONF_DIR", "non_existing");
		String conf = configure(Geobricks.ATTR_CONF_DIR, String.class);
		assertEquals(DEFAULT_CONFIG_PATH, conf);
	}

	@Test
	public void nonExistingConfDirForApp() throws Exception {
		JSONObject json = JSONObject.fromObject("{" + PLUGIN_ID + ": {}}");
		when(context.getAttribute(Geobricks.ATTR_PLUGINS_CONF))
				.thenReturn(json);

		System.setProperty("GEOBRICKS_CONF_DIR",
				System.getProperty("java.io.tmpdir"));
		String conf = configure(Geobricks.ATTR_CONF_DIR, String.class);
		assertEquals(DEFAULT_CONFIG_PATH, conf);
	}

	@Test
	public void confDirFromVariable() throws Exception {
		JSONObject json = JSONObject.fromObject("{" + PLUGIN_ID + ": {}}");
		when(context.getAttribute(Geobricks.ATTR_PLUGINS_CONF))
				.thenReturn(json);

		File dir = new File(System.getProperty("java.io.tmpdir"), APP_NAME);
		dir.mkdir();

		System.setProperty("GEOBRICKS_CONF_DIR", dir.getParent());
		String conf = configure(Geobricks.ATTR_CONF_DIR, String.class);
		assertEquals(dir.getAbsolutePath(), conf);

		dir.delete();
	}

	@Test
	public void getConfiguredAppId() throws Exception {
		String configuredId = "configuredid";
		when(context.getInitParameter("geobricks-app-id")).thenReturn(
				configuredId);
		when(context.getContextPath()).thenReturn("/myapp");

		assertEquals(listener.getApplicationId(context), configuredId);
	}

	@Test
	public void getAppIdFromURL() throws Exception {
		String urlId = "myapp";
		when(context.getInitParameter("geobricks-app-id")).thenReturn(null);
		when(context.getContextPath()).thenReturn("/" + urlId);

		assertEquals(urlId, listener.getApplicationId(context));
	}

	@Test
	public void pluginDescriptorOrder() throws Exception {
		PluginDescriptor p1 = new PluginDescriptor("p1");
		PluginDescriptor p2 = new PluginDescriptor("p2");

		when(context.getAttribute(Geobricks.ATTR_PLUGINS_DESC)).thenReturn(
				new PluginDescriptor[] { p1, p2 });

		JSONObject json = JSONObject.fromObject("{" + p2.getId() + ": {}, "
				+ p1.getId() + " :{}}");
		when(context.getAttribute(Geobricks.ATTR_PLUGINS_CONF))
				.thenReturn(json);

		PluginDescriptor[] descriptors = configure(Geobricks.ATTR_PLUGINS_DESC,
				PluginDescriptor[].class);
		assertEquals(p2.getId(), descriptors[0].getId());
		assertEquals(p1.getId(), descriptors[1].getId());
	}

	@Test
	public void onlyIncludePluginsOnAppConf() throws Exception {
		// Only PLUGIN_ID defined
		JSONObject json = JSONObject.fromObject("{" + PLUGIN_ID + ": {}}");
		when(context.getAttribute(Geobricks.ATTR_PLUGINS_CONF))
				.thenReturn(json);

		// Several plugins on classpath
		PluginDescriptor p1 = new PluginDescriptor(PLUGIN_ID);
		PluginDescriptor p2 = new PluginDescriptor(PLUGIN_ID + "2");
		when(context.getAttribute(Geobricks.ATTR_PLUGINS_DESC)).thenReturn(
				new PluginDescriptor[] { p1, p2 });

		PluginDescriptor[] descriptors = configure(Geobricks.ATTR_PLUGINS_DESC,
				PluginDescriptor[].class);
		assertEquals(1, descriptors.length);
		assertEquals(PLUGIN_ID, descriptors[0].getId());
	}

	private <T extends Object> T configure(String attribute, Class<T> clazz)
			throws Exception {
		listener.configureContext(context);
		ArgumentCaptor<T> captor = ArgumentCaptor.forClass(clazz);
		verify(context).setAttribute(eq(attribute), captor.capture());
		return captor.getValue();
	}
}
