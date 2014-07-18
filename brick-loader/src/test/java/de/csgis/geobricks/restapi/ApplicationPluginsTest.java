package de.csgis.geobricks.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.guice.RuntimeModule;
import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.ApplicationPluginUsage;

public class ApplicationPluginsTest {
	private static final String APP_ID = "viewer";
	private static final String PLUGIN_ID = "plugin";
	private static final String PLUGIN_DEFAULT_CONF = "{ 'plugin': {}}";

	@Inject
	private Applications apps;

	@Inject
	private ApplicationPlugins plugins;

	@Inject
	private PluginRegistry pluginRegistry;

	@Before
	public void installApp() throws ClientProtocolException, IOException,
			ApplicationNotFoundException {
		Guice.createInjector(new RuntimeModule()).injectMembers(this);

		try {
			apps.delete(APP_ID);
		} catch (ApplicationNotFoundException e) {
			// ignore
		}

		apps.put(APP_ID);
		plugins = apps.getApplicationPlugins(APP_ID);

		PluginDescriptor plugin = mock(PluginDescriptor.class);
		when(plugin.getId()).thenReturn(PLUGIN_ID);
		when(plugin.getDefaultConfiguration()).thenReturn(PLUGIN_DEFAULT_CONF);
		pluginRegistry.putPlugin(plugin);

	}

	@Test
	public void getPluginListForApp() throws Exception {
		Application app = apps.get(APP_ID);

		assertEquals(0, app.getPlugins().size());

		plugins.put(PLUGIN_ID);

		app = apps.get(APP_ID);
		assertEquals(1, app.getPlugins().size());
		ApplicationPluginUsage first = app.getPlugins().iterator().next();
		assertEquals(PLUGIN_ID, first.getPluginId());
	}

	@Test
	public void putAndGetPlugin() throws Exception {
		plugins.put(PLUGIN_ID);
		assertEquals(PLUGIN_ID, plugins.get(PLUGIN_ID).getPluginId());

	}

	@Test
	public void putPluginDefaultConfiguration() throws Exception {
		plugins.put(PLUGIN_ID);
		assertEquals(PLUGIN_DEFAULT_CONF, plugins.get(PLUGIN_ID)
				.getConfiguration());
	}

	@Test
	public void putOnlyValidJSON() throws Exception {
		try {
			plugins.put(PLUGIN_ID, "{invalidjson:}");
			fail();
		} catch (InvalidPluginConfiguration e) {
		}
	}

	@Test
	public void whatYouPUTIsWhatYouGET() throws Exception {
		String config = "{center: { lat : 40.8, lon : -73.96, zoomLevel : 10},"
				+ "olmap : { div : 'layout-center' }}";
		plugins.put(PLUGIN_ID, config);
		assertEquals(config, plugins.get(PLUGIN_ID).getConfiguration());
	}

	@Test
	public void getNonExistingPlugin() throws Exception {
		try {
			plugins.get("nonexistingplugin");
			fail();
		} catch (PluginNotFoundException e) {
		}
	}

	@Test
	public void putNonExistingPlugin() throws Exception {
		try {
			plugins.put("nonexistinplugin");
			fail();
		} catch (PluginNotAvailableException e) {
		}
	}

	@Test
	public void deleteNonExistingPlugin() throws Exception {
		try {
			plugins.delete("nonexistinplugin");
			fail();
		} catch (PluginNotFoundException e) {
		}
	}

	@Test
	public void putExistingPlugin() throws Exception {
		plugins.put(PLUGIN_ID, "{}");
		assertEquals("{}", plugins.get(PLUGIN_ID).getConfiguration());

		plugins.put(PLUGIN_ID, "[]");
		assertEquals("[]", plugins.get(PLUGIN_ID).getConfiguration());
	}

	@Test
	public void deletePlugin() throws Exception {
		// Put
		plugins.put(PLUGIN_ID);

		// Check it's there
		plugins.get(PLUGIN_ID);

		// Delete
		plugins.delete(PLUGIN_ID);

		// Check it's not there
		try {
			plugins.get(PLUGIN_ID);
			fail();
		} catch (PluginNotFoundException e) {
		}
	}
}
