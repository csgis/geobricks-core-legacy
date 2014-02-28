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
import org.junit.BeforeClass;
import org.junit.Test;

public class ApplicationManagementTest {

	private static final String APP_NAME = "apps";
	private static final int PORT = 9090;
	private static final String BASE_URL = "http://localhost:" + PORT + "/"
			+ APP_NAME + "/";
	private static Server server;

	@BeforeClass
	public static void start() throws Exception {
		server = new Server(PORT);
		server.setStopAtShutdown(true);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/" + APP_NAME);
		webAppContext.setResourceBase("src/main/webapp");
		webAppContext.setClassLoader(Server.class.getClassLoader());
		server.setHandler(webAppContext);

		server.start();
	}

	@AfterClass
	public static void stop() throws Exception {
		server.stop();
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
		return execute(new HttpPut(BASE_URL + path));
	}

	private int doDelete(String path) throws IOException,
			ClientProtocolException {
		return execute(new HttpDelete(BASE_URL + path));
	}

	private int doGet(String path) throws MalformedURLException, IOException {
		return execute(new HttpGet(BASE_URL + path));
	}

	private int execute(HttpRequestBase put) throws IOException,
			ClientProtocolException {
		HttpClient client = HttpClients.createDefault();
		HttpResponse response = client.execute(put);
		return response.getStatusLine().getStatusCode();
	}

}
