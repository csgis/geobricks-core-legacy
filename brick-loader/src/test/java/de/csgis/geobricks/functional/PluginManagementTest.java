package de.csgis.geobricks.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.Path;
import de.csgis.geobricks.addressSearch.AddressSearchPlugin;
import de.csgis.geobricks.layerList.LayerListPlugin;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.olmap.OLMapPlugin;

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
		assertTrue(array.contains(OLMapPlugin.ID));
		assertTrue(array.contains(LayerListPlugin.ID));
		assertTrue(array.contains(LayoutPlugin.ID));
		assertTrue(array.contains(AddressSearchPlugin.ID));
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

		plugins.doPut(OLMapPlugin.ID);
		array = TestUtils.parseJsonArray(plugins.doGet());
		assertEquals(1, array.size());
		assertEquals(OLMapPlugin.ID, array.get(0));
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
		plugins.doPut(OLMapPlugin.ID);

		HttpResponse doGet = plugins.doGet(OLMapPlugin.ID);

		assertTrue(doGet.getEntity().getContentType().toString()
				.contains("json"));
		String pluginConfiguration = JSONSerializer.toJSON(
				IOUtils.toString(doGet.getEntity().getContent())).toString();
		String defaultPluginConfiguration = JSONSerializer.toJSON(
				new OLMapPlugin().getDefaultConfiguration()).toString();
		assertEquals(pluginConfiguration, defaultPluginConfiguration);
	}

	@Test
	public void putOnlyValidJSON() throws Exception {
		HttpResponse response = plugins.doPut(OLMapPlugin.ID, "{invalidjson:}");
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 400);
	}

	@Test
	public void whatYouPUTIsWhatYouGET() throws Exception {
		String jsonRequest = "{center: { lat : 40.8, lon : -73.96, zoomLevel : 10},"
				+ "olmap : { div : 'layout-center' }}";
		plugins.doPut(OLMapPlugin.ID, jsonRequest);

		HttpResponse doGet = plugins.doGet(OLMapPlugin.ID);

		JSON jsonResponse = JSONSerializer.toJSON(IOUtils.toString(doGet
				.getEntity().getContent()));
		assertEquals(jsonResponse, JSONSerializer.toJSON(jsonRequest));
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
		HttpResponse response = plugins.doPut(OLMapPlugin.ID);
		assertEquals(204, response.getStatusLine().getStatusCode());

		response = plugins.doGet(OLMapPlugin.ID);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void putPluginNonExistingApp() throws Exception {
		HttpResponse response = apps.doPut("nonexistingapp/plugins/p1");
		assertEquals(404, response.getStatusLine().getStatusCode());
	}

	@Test
	public void putExistingPlugin() throws Exception {
		HttpResponse response = plugins.doPut(OLMapPlugin.ID);
		assertEquals(204, response.getStatusLine().getStatusCode());
		response = plugins.doPut(OLMapPlugin.ID);
		assertEquals(204, response.getStatusLine().getStatusCode());
	}

	@Test
	public void deletePlugin() throws Exception {
		// Put
		HttpResponse response = plugins.doPut(OLMapPlugin.ID);
		assertEquals(204, response.getStatusLine().getStatusCode());

		// Check it's there
		response = plugins.doGet(OLMapPlugin.ID);
		assertEquals(200, response.getStatusLine().getStatusCode());

		// Delete
		response = plugins.doDelete(OLMapPlugin.ID);
		assertEquals(204, response.getStatusLine().getStatusCode());

		// Check it's not there
		response = plugins.doGet(OLMapPlugin.ID);
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
		plugins.doPut(OLMapPlugin.ID);

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
