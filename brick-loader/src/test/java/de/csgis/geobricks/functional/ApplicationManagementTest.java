package de.csgis.geobricks.functional;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApplicationManagementTest {

	private static final String LOADER_APP_NAME = "geobricks";
	private static final int PORT = 9090;
	private static final String BASE_URL = "http://localhost:" + PORT + "/"
			+ LOADER_APP_NAME + "/";
	private static final String APP_BASE_URL = BASE_URL + "apps/";

	private static Server server;

	@BeforeClass
	public static void start() throws Exception {
		server = new Server(PORT);
		server.setStopAtShutdown(true);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/" + LOADER_APP_NAME);
		webAppContext.setResourceBase("src/main/webapp");
		webAppContext.setOverrideDescriptor("src/test/resources/test-web.xml");
		// TODO can this be removed?
		webAppContext.setClassLoader(ApplicationManagementTest.class
				.getClassLoader());
		server.setHandler(webAppContext);

		server.start();
	}

	@AfterClass
	public static void stop() throws Exception {
		server.stop();
	}

	@Before
	public void cleanDatabase() throws Exception {
		doDelete("stadtplan");
	}

	@Test
	public void putApplication() throws Exception {
		/*
		 * Initial check to see the url does not exist
		 */
		assertEquals(404, doGet("stadtplan"));

		/*
		 * PUT new application in root
		 */
		int statusCode = doPut("stadtplan");
		assertEquals(204, statusCode);

		/*
		 * Check the url now exists
		 */
		assertEquals(200, doGet("stadtplan"));
	}

	@Test
	public void deleteApplication() throws Exception {
		/*
		 * Add the application
		 */
		assertEquals(204, doPut("stadtplan"));

		/*
		 * Remove it
		 */
		int statusCode = doDelete("stadtplan");
		assertEquals(204, statusCode);

		/*
		 * Check the url now exists
		 */
		assertEquals(404, doGet("stadtplan"));
	}

	@Test
	public void deleteUnexistentApplication() throws Exception {
		/*
		 * Remove it
		 */
		int statusCode = doDelete("doesnotexist");
		assertEquals(404, statusCode);
	}

	private int doPut(String path) throws IOException, ClientProtocolException {
		return execute(new HttpPut(APP_BASE_URL + path));
	}

	private int doDelete(String path) throws IOException,
			ClientProtocolException {
		return execute(new HttpDelete(APP_BASE_URL + path));
	}

	private int doGet(String path) throws MalformedURLException, IOException {
		return execute(new HttpGet(APP_BASE_URL + path));
	}

	private int execute(HttpRequestBase put) throws IOException,
			ClientProtocolException {
		HttpClient client = HttpClients.createDefault();
		HttpResponse response = client.execute(put);
		return response.getStatusLine().getStatusCode();
	}

}
