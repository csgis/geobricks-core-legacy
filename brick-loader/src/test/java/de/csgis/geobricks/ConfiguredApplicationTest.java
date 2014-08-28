package de.csgis.geobricks;

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

import com.google.inject.Injector;

public class ConfiguredApplicationTest {
	private static final String REAL_PATH = "";
	private static final String DEFAULT_CONFIG_PATH = REAL_PATH
			+ File.separator + "WEB-INF" + File.separator + "default_config";
	private static final String PLUGIN_ID = "myplugin";
	private static final String APP_NAME = "geobricks_testing_app_conf_dir";

	private ServletContext context;
	private PluginRegistry registry;
	private PluginDescriptor plugin;
	private ConfiguredApplication listener;

	@Before
	public void setup() {
		listener = new ConfiguredApplication();
		context = mock(ServletContext.class);
		registry = mock(PluginRegistry.class);
		Injector injector = mock(Injector.class);

		when(injector.getInstance(PluginRegistry.class)).thenReturn(registry);
		when(context.getAttribute(Injector.class.getCanonicalName()))
				.thenReturn(injector);
		when(context.getRealPath(anyString())).thenReturn(REAL_PATH);
		when(context.getContextPath()).thenReturn(APP_NAME);

		plugin = mock(PluginDescriptor.class);
		when(plugin.getId()).thenReturn(PLUGIN_ID);
		when(registry.getPlugin(anyString())).thenReturn(plugin);
	}

	@Test
	public void customPluginConfig() throws Exception {
		mockConfig("{" + PLUGIN_ID + ": { mymodule : { enabled : true }}}");
		JSONObject conf = configure(ConfiguredApplication.ATTR_PLUGINS_CONF,
				JSONObject.class);
		assertTrue(conf.getJSONObject(PLUGIN_ID).getJSONObject("mymodule")
				.getBoolean("enabled"));
	}

	@Test
	public void defaultPluginConfigWhenEmpty() throws Exception {
		when(plugin.getDefaultConfiguration()).thenReturn(
				"{ mymodule : { enabled : false }}");

		mockConfig("{" + PLUGIN_ID + ": {}}");
		JSONObject conf = configure(ConfiguredApplication.ATTR_PLUGINS_CONF,
				JSONObject.class);
		assertFalse(conf.getJSONObject(PLUGIN_ID).getJSONObject("mymodule")
				.getBoolean("enabled"));
	}

	@Test
	public void undefinedConfDir() throws Exception {
		mockConfig("{" + PLUGIN_ID + ": {}}");
		String conf = configure(Geobricks.CONF_DIR_ATTRIBUTE, String.class);
		assertEquals(DEFAULT_CONFIG_PATH, conf);
	}

	@Test
	public void nonExistingConfDirBase() throws Exception {
		mockConfig("{" + PLUGIN_ID + ": {}}");
		System.setProperty("GEOBRICKS_CONF_DIR", "non_existing");
		String conf = configure(Geobricks.CONF_DIR_ATTRIBUTE, String.class);
		assertEquals(DEFAULT_CONFIG_PATH, conf);
	}

	@Test
	public void nonExistingConfDirForApp() throws Exception {
		mockConfig("{" + PLUGIN_ID + ": {}}");
		System.setProperty("GEOBRICKS_CONF_DIR",
				System.getProperty("java.io.tmpdir"));
		String conf = configure(Geobricks.CONF_DIR_ATTRIBUTE, String.class);
		assertEquals(DEFAULT_CONFIG_PATH, conf);
	}

	@Test
	public void confDirFromVariable() throws Exception {
		mockConfig("{" + PLUGIN_ID + ": {}}");
		File dir = new File(System.getProperty("java.io.tmpdir"), APP_NAME);
		dir.mkdir();

		System.setProperty("GEOBRICKS_CONF_DIR", dir.getParent());
		String conf = configure(Geobricks.CONF_DIR_ATTRIBUTE, String.class);
		assertEquals(dir.getAbsolutePath(), conf);

		dir.delete();
	}

	private void mockConfig(String json) {
		when(context.getResourceAsStream(anyString())).thenReturn(
				new ByteArrayInputStream(json.getBytes()));
	}

	private <T extends Object> T configure(String attribute, Class<T> clazz)
			throws Exception {
		listener.configureContext(context);
		ArgumentCaptor<T> captor = ArgumentCaptor.forClass(clazz);
		verify(context).setAttribute(eq(attribute), captor.capture());
		return captor.getValue();
	}
}
