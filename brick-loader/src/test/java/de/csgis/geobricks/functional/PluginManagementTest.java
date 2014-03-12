package de.csgis.geobricks.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.csgis.geobricks.Geobricks;

public class PluginManagementTest extends AbstractFunctionalTest {
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
		String appsBase = Geobricks.ADMIN_ROOT + "/" + Geobricks.APPS_ROOT;
		String pluginsBase = Geobricks.ADMIN_ROOT + "/"
				+ Geobricks.PLUGINS_ROOT;
		String appBase = appsBase + "/" + APP_ID;

		apps = new RestPoint(serverManager, appsBase);
		pluginList = new RestPoint(serverManager, pluginsBase);

		apps.doDelete(APP_ID);
		apps.doPut(APP_ID);

		app = new RestPoint(serverManager, appBase);
		plugins = new RestPoint(serverManager, appBase + "/"
				+ Geobricks.PLUGINS_ROOT);
	}

	@Test
	public void getPluginList() throws Exception {
		JSONArray array = parseJsonArray(pluginList.doGet());
		assertEquals(1, array.size());
		assertEquals("hello", array.getString(0));
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
		JSONArray array = parseJsonArray(plugins.doGet());
		assertEquals(0, array.size());

		String pluginId = "p1";
		plugins.doPut(pluginId);
		array = parseJsonArray(plugins.doGet());
		assertEquals(1, array.size());
		assertEquals(pluginId, array.get(0));
	}

	@Test
	public void getPluginListNonExistingApp() throws Exception {
		HttpResponse response = apps.doGet("nonexistingapp/plugins");
		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void putPluginListForApp() throws Exception {
		HttpResponse response = app.doPut(Geobricks.PLUGINS_ROOT);
		assertEquals(405, response.getStatusLine().getStatusCode());
	}

	@Test
	public void deletePluginListForApp() throws Exception {
		HttpResponse response = app.doDelete(Geobricks.PLUGINS_ROOT);
		assertEquals(405, response.getStatusLine().getStatusCode());
	}

	@Test
	public void getPlugin() throws Exception {
		String pluginId = "p1";
		plugins.doPut(pluginId);

		JSONObject plugin = parseJsonObject(plugins.doGet(pluginId));
		assertTrue(plugin.has("id"));
		assertEquals(pluginId, plugin.get("id"));
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
		String pluginId = "p1";
		HttpResponse response = plugins.doPut(pluginId);
		assertEquals(204, response.getStatusLine().getStatusCode());

		response = plugins.doGet(pluginId);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void putPluginNonExistingApp() throws Exception {
		HttpResponse response = apps.doPut("nonexistingapp/plugins/p1");
		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void putExistingPlugin() throws Exception {
		String pluginId = "p1";
		HttpResponse response = plugins.doPut(pluginId);
		assertEquals(204, response.getStatusLine().getStatusCode());
		response = plugins.doPut(pluginId);
		assertEquals(204, response.getStatusLine().getStatusCode());
	}

	@Test
	public void deletePlugin() throws Exception {
		String pluginId = "p1";
		// Put
		HttpResponse response = plugins.doPut(pluginId);
		assertEquals(204, response.getStatusLine().getStatusCode());

		// Check it's there
		response = plugins.doGet(pluginId);
		assertEquals(200, response.getStatusLine().getStatusCode());

		// Delete
		response = plugins.doDelete(pluginId);
		assertEquals(204, response.getStatusLine().getStatusCode());

		// Check it's not there
		response = plugins.doGet(pluginId);
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
	@Ignore
	public void testAddPlugin() throws Exception {
		plugins.doPut("hello");

		// Check the application has a call to config.js
		HttpResponse response = apps.doGet("stadtplan");
		String content = IOUtils.toString(response.getEntity().getContent());
		assertTrue(content.contains("config.js"));

		// Check that the call to config.js contains the reference to the plugin
		response = apps.doGet("stadtplan/config.js");
		content = IOUtils.toString(response.getEntity().getContent());
		assertTrue(content.contains("hello"));
	}

	@Override
	protected String getRoot() {
		return Geobricks.PLUGINS_ROOT;
	}
}
