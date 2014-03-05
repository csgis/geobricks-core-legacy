package de.csgis.geobricks.functional;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PluginManagementTest {

	private static ServerManager serverManager = new ServerManager();

	@BeforeClass
	public static void start() throws Exception {
		serverManager.start("geobricks");
	}

	@AfterClass
	public static void stop() throws Exception {
		serverManager.stop();
	}

	private RestPoint apps;
	private RestPoint plugins;

	@Before
	public void installApp() throws ClientProtocolException, IOException {
		apps = new RestPoint(serverManager, "apps");
		apps.doPut("stadtplan");
		plugins = new RestPoint(serverManager, "apps/stadtplan/plugins");
	}

	@Test
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

}
