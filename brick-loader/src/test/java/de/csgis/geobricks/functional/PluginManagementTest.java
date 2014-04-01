package de.csgis.geobricks.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.Path;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.olmap.OLMapPlugin;
import de.csgis.geobricks.title.TitlePlugin;

public class PluginManagementTest {
	private static ServerManager serverManager = new ServerManager();

	private static final String APP_ID = "stadtplan";

	private RestPoint apps, pluginList, app, plugins;

	@BeforeClass
	public static void start() throws Exception {
		serverManager.start("geobricks");
	}

	@AfterClass
	public static void stop() throws Exception {
		serverManager.stop();
	}

	@Before
	public void installApp() throws ClientProtocolException, IOException {
		String appBase = Geobricks.root.rest().app(APP_ID).path();

		apps = new RestPoint(serverManager, Geobricks.root.rest().apps().path());
		pluginList = new RestPoint(serverManager, Geobricks.root.rest()
				.plugins().path());

		apps.doDelete(APP_ID);
		apps.doPut(APP_ID);

		app = new RestPoint(serverManager, appBase);
		plugins = new RestPoint(serverManager, Geobricks.root.rest()
				.app(APP_ID).plugins().path());
	}

	@Test
	public void getPluginList() throws Exception {
		JSONArray array = TestUtils.parseJsonArray(pluginList.doGet());
		assertTrue(array.contains(OLMapPlugin.NAME));
		assertTrue(array.contains(LayerListPlugin.NAME));
		assertTrue(array.contains(LayoutPlugin.NAME));
		assertTrue(array.contains(TitlePlugin.NAME));
		assertTrue(array.contains(AddressSearchPlugin.NAME));
		assertTrue(false);
	}

	@Test
	public void putPluginList() throws Exception {
		assertEquals(405, pluginList.doPut().getStatusLine().getStatusCode());
	}

	@Test
	public void deletePluginList() throws Exception {
		assertEquals(405, pluginList.doDelete().getStatusLine().getStatusCode());
	}

	@Test
	public void getPluginListForApp() throws Exception {
		JSONArray array = TestUtils.parseJsonArray(plugins.doGet());
		assertEquals(0, array.size());

		plugins.doPut(OLMapPlugin.NAME);
		array = TestUtils.parseJsonArray(plugins.doGet());
		assertEquals(1, array.size());
		assertEquals(OLMapPlugin.NAME, array.get(0));
	}

	@Test
	public void getPluginListNonExistingApp() throws Exception {
		HttpResponse response = apps.doGet("nonexistingapp/plugins");
		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void putPluginListForApp() throws Exception {
		HttpResponse response = app.doPut(Path.PLUGINS);
		assertEquals(405, response.getStatusLine().getStatusCode());
	}

	@Test
	public void deletePluginListForApp() throws Exception {
		HttpResponse response = app.doDelete(Path.PLUGINS);
		assertEquals(405, response.getStatusLine().getStatusCode());
	}

	@Test
	public void getPlugin() throws Exception {
		plugins.doPut(OLMapPlugin.NAME);

		JSONObject plugin = TestUtils.parseJsonObject(plugins
				.doGet(OLMapPlugin.NAME));
		assertTrue(plugin.has("id"));
		assertEquals(OLMapPlugin.NAME, plugin.get("id"));
	}

	@Test
	public void getPluginNonExistingApp() throws Exception {
		HttpResponse response = apps.doGet("nonexistingapp/plugins/p1");
		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void getNonExistingPlugin() throws Exception {
		HttpResponse response = plugins.doGet("nonexistingplugin");
		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void putPlugin() throws Exception {
		HttpResponse response = plugins.doPut(OLMapPlugin.NAME);
		assertEquals(204, response.getStatusLine().getStatusCode());

		response = plugins.doGet(OLMapPlugin.NAME);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void putPluginNonExistingApp() throws Exception {
		HttpResponse response = apps.doPut("nonexistingapp/plugins/p1");
		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void putExistingPlugin() throws Exception {
		HttpResponse response = plugins.doPut(OLMapPlugin.NAME);
		assertEquals(204, response.getStatusLine().getStatusCode());
		response = plugins.doPut(OLMapPlugin.NAME);
		assertEquals(204, response.getStatusLine().getStatusCode());
	}

	@Test
	public void deletePlugin() throws Exception {
		// Put
		HttpResponse response = plugins.doPut(OLMapPlugin.NAME);
		assertEquals(204, response.getStatusLine().getStatusCode());

		// Check it's there
		response = plugins.doGet(OLMapPlugin.NAME);
		assertEquals(200, response.getStatusLine().getStatusCode());

		// Delete
		response = plugins.doDelete(OLMapPlugin.NAME);
		assertEquals(204, response.getStatusLine().getStatusCode());

		// Check it's not there
		response = plugins.doGet(OLMapPlugin.NAME);
		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void deletePluginNonExistingApp() throws Exception {
		HttpResponse response = apps.doDelete("nonexistingapp/plugins/p1");
		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void deleteNonExistingPlugin() throws Exception {
		HttpResponse response = plugins.doDelete("nonexistinplugin");
		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void testAddPlugin() throws Exception {
		plugins.doPut(OLMapPlugin.NAME);

		// Check the application has a call to config.js
		RestPoint clientApps = new RestPoint(serverManager, Geobricks.root
				.apps().path());
		HttpResponse response = clientApps.doGet("stadtplan");
		String content = IOUtils.toString(response.getEntity().getContent());
		assertTrue(content.contains("config.js"));

		// Check that the call to config.js contains the reference to the plugin
		response = clientApps.doGet("stadtplan/config.js");
		content = IOUtils.toString(response.getEntity().getContent());
		JSONObject config = JSONObject.fromObject(content.replace(
				"var require = ", ""));
		JSONArray array = config.getJSONObject("config").getJSONArray("main");
		assertTrue(array.contains("olmap"));
	}

	@Test
	public void putNonExistingPlugin() throws Exception {
		HttpResponse response = plugins.doPut("non_existing_plugin");
		assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatusLine()
				.getStatusCode());
	}
}
