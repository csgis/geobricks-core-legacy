package de.csgis.geobricks.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.addressSearch.AddressSearchPlugin;

public class GetApplicationTest {
	private static ServerManager serverManager = new ServerManager();

	private static final String APP_ID = "stadtplan";

	private static RestPoint app;

	@BeforeClass
	public static void start() throws Exception {
		serverManager.start("geobricks");
		app = new RestPoint(serverManager, Geobricks.root.app(APP_ID).path());

	}

	@AfterClass
	public static void stop() throws Exception {
		serverManager.stop();
	}

	@Before
	public void installApp() throws ClientProtocolException, IOException {
		RestPoint restApps = new RestPoint(serverManager, Geobricks.root.rest()
				.apps().path());
		restApps.doDelete(APP_ID);
		restApps.doPut(APP_ID);
	}

	@Test
	public void indexHTML() throws Exception {
		HttpResponse response = app.doGet();

		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine()
				.getStatusCode());

		String content = IOUtils.toString(response.getEntity().getContent());

		assertTrue(content.trim().startsWith("<html>"));
		assertTrue(content.contains("config.js"));
	}

	@Test
	public void libJQuery() throws Exception {
		String path = "jslib/jquery-1.11.0.min.js";
		HttpResponse response = app.doGet(path);

		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine()
				.getStatusCode());

		InputStream original = getClass().getResourceAsStream(
				"../webapp/" + path);
		InputStream responseContent = response.getEntity().getContent();

		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine()
				.getStatusCode());
		assertTrue(IOUtils.contentEquals(original, responseContent));
	}

	@Test
	public void moduleMain() throws Exception {
		HttpResponse response = app.doGet("modules/main.js");

		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine()
				.getStatusCode());
		String line = IOUtils.toString(response.getEntity().getContent());
		assertTrue(line.trim().startsWith("require.config({"));
	}

	@Test
	public void moduleMessageBus() throws Exception {
		HttpResponse response = app.doGet("modules/message-bus.js");

		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine()
				.getStatusCode());
		String line = IOUtils.toString(response.getEntity().getContent());
		assertTrue(line.trim().startsWith("define(["));
	}

	@Test
	public void indexHTMLContainsCSS() throws Exception {
		RestPoint plugins = new RestPoint(serverManager, Geobricks.root.rest()
				.app(APP_ID).plugins().path());
		plugins.doPut(new AddressSearchPlugin().getName());

		HttpResponse response = app.doGet();
		String content = IOUtils.toString(response.getEntity().getContent());
		Pattern pattern = Pattern.compile("<link[^>]*address-search.css[^>]/>");
		assertTrue(pattern.matcher(content).find());
	}

	@Test
	public void indexHTMLNoBootstrapCSS() throws Exception {
		HttpResponse response = app.doGet();
		String content = IOUtils.toString(response.getEntity().getContent());
		Pattern pattern = Pattern.compile("<link[^>]*bootstrap.*[^>]/>");
		assertFalse(pattern.matcher(content).find());
	}

}
