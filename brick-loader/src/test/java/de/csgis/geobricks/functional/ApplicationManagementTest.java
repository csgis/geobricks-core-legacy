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

public class ApplicationManagementTest extends AbstractFunctionalTest {
	private static ServerManager serverManager = new ServerManager();

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
		doDelete("stadtplan");
	}

	@Test
	public void getList() throws Exception {
		HttpResponse response = doGet("");
		JSONArray array = parseJsonArray(response);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals(0, array.size());

		doPut("stadtplan");

		response = doGet("");
		array = parseJsonArray(response);
		assertEquals(1, array.size());
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertEquals("stadtplan", array.get(0));
	}

	@Test
	public void getApplication() throws Exception {
		doPut("stadtplan");

		HttpResponse response = doGet("stadtplan");
		JSONObject obj = parseJsonObject(response);
		assertEquals(200, response.getStatusLine().getStatusCode());
		assertTrue(obj.has("id"));
		assertEquals("stadtplan", obj.get("id"));
	}

	@Test
	public void getNonExistingApplication() throws Exception {
		assertEquals(404, doGetStatus("stadtplan"));
	}

	@Test
	public void putApplication() throws Exception {
		/*
		 * PUT new application in root
		 */
		assertEquals(204, doPutStatus("stadtplan"));

		/*
		 * Check the url now exists
		 */
		assertEquals(200, doGetStatus("stadtplan"));
	}

	@Test
	public void putExistingApplication() throws Exception {
		assertEquals(204, doPutStatus("stadtplan"));
		assertEquals(200, doGetStatus("stadtplan"));
		assertEquals(204, doPutStatus("stadtplan"));
		assertEquals(200, doGetStatus("stadtplan"));
	}

	@Test
	public void putBaseUrl() throws Exception {
		HttpResponse response = doPut("");
		assertEquals(405, response.getStatusLine().getStatusCode());
	}

	@Test
	public void deleteApplication() throws Exception {
		/*
		 * Add the application
		 */
		assertEquals(204, doPutStatus("stadtplan"));

		/*
		 * Remove it
		 */
		assertEquals(204, doDeleteStatus("stadtplan"));

		/*
		 * Check the url does not exist
		 */
		assertEquals(404, doGetStatus("stadtplan"));
	}

	@Test
	public void deleteUnexistentApplication() throws Exception {
		assertEquals(404, doDeleteStatus("doesnotexist"));
	}

	@Test
	public void deleteBaseUrl() throws Exception {
		HttpResponse response = doDelete("");
		assertEquals(405, response.getStatusLine().getStatusCode());
	}

	@Override
	protected String getRoot() {
		return Geobricks.APPS_ROOT;
	}
}
