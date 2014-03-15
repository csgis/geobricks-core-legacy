package de.csgis.geobricks.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.csgis.geobricks.Geobricks;

public class ApplicationManagementTest extends TestUtils {
	private static ServerManager serverManager = new ServerManager();

	private static final String APP_ID = "stadtplan";

	private RestPoint apps;

	@BeforeClass
	public static void start() throws Exception {
		serverManager.start("geobricks");
	}

	@AfterClass
	public static void stop() throws Exception {
		serverManager.stop();
	}

	@Before
	public void cleanDatabase() throws Exception {
		apps = new RestPoint(serverManager, Geobricks.root.rest().apps().path());
		apps.doDelete(APP_ID);
	}

	@Test
	public void getList() throws Exception {
		HttpResponse response = apps.doGet();
		JSONArray array = parseJsonArray(response);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(0, array.size());

		apps.doPut(APP_ID);

		response = apps.doGet();
		array = parseJsonArray(response);
		assertEquals(1, array.size());
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(APP_ID, array.getString(0));
	}

	@Test
	public void getApplication() throws Exception {
		apps.doPut(APP_ID);

		HttpResponse response = apps.doGet(APP_ID);
		JSONObject obj = parseJsonObject(response);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertTrue(obj.has("id"));
		assertEquals(APP_ID, obj.get("id"));
	}

	@Test
	public void getNonExistingApplication() throws Exception {
		assertEquals(404, apps.doGet("none").getStatusLine().getStatusCode());
	}

	@Test
	public void putApplication() throws Exception {
		assertEquals(204, apps.doPut(APP_ID).getStatusLine().getStatusCode());
		assertEquals(200, apps.doGet(APP_ID).getStatusLine().getStatusCode());
	}

	@Test
	public void putExistingApplication() throws Exception {
		assertEquals(204, apps.doPut(APP_ID).getStatusLine().getStatusCode());
		assertEquals(200, apps.doGet(APP_ID).getStatusLine().getStatusCode());
		assertEquals(204, apps.doPut(APP_ID).getStatusLine().getStatusCode());
		assertEquals(200, apps.doGet(APP_ID).getStatusLine().getStatusCode());
	}

	@Test
	public void putBaseUrl() throws Exception {
		assertEquals(405, apps.doPut().getStatusLine().getStatusCode());
	}

	@Test
	public void deleteApplication() throws Exception {
		assertEquals(204, apps.doPut(APP_ID).getStatusLine().getStatusCode());
		assertEquals(204, apps.doDelete(APP_ID).getStatusLine().getStatusCode());
		assertEquals(404, apps.doGet(APP_ID).getStatusLine().getStatusCode());
	}

	@Test
	public void deleteUnexistentApplication() throws Exception {
		assertEquals(404, apps.doDelete("none").getStatusLine().getStatusCode());
	}

	@Test
	public void deleteBaseUrl() throws Exception {
		assertEquals(405, apps.doDelete().getStatusLine().getStatusCode());
	}
}
