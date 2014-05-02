package de.csgis.geobricks.functional;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.addressSearch.AddressSearchPlugin;

public class StaticServletTest {
	private static ServerManager serverManager = new ServerManager();
	private static RestPoint app;

	private static final String APP_ID = "stadtplan";

	@BeforeClass
	public static void start() throws Exception {
		serverManager.start("geobricks");
		app = new RestPoint(serverManager, Geobricks.root.app(APP_ID).path());
	}

	@Before
	public void installApp() throws ClientProtocolException, IOException {
		RestPoint restApps = new RestPoint(serverManager, Geobricks.root.rest()
				.apps().path());
		restApps.doDelete(APP_ID);
		restApps.doPut(APP_ID);
	}

	@AfterClass
	public static void stop() throws Exception {
		serverManager.stop();
	}

	@Test
	public void getImage() throws Exception {
		// This image is on the address-search plugin
		String img = "close.png";

		File original = File.createTempFile("close", ".png");
		InputStream originalStream = AddressSearchPlugin.class
				.getResourceAsStream("/de/csgis/geobricks/webapp/images/" + img);
		IOUtils.copy(originalStream, new FileOutputStream(original));

		HttpResponse response = app.doGet("images/" + img);
		File tmp = File.createTempFile("close", ".png");
		IOUtils.copy(response.getEntity().getContent(), new FileOutputStream(
				tmp));

		assertEquals(original.length(), tmp.length());
		assertEquals(FileUtils.checksumCRC32(original),
				FileUtils.checksumCRC32(tmp));

		tmp.delete();
		original.delete();
	}

	@Test
	public void getFileOnSubdirectory() throws Exception {
		// This js lib is on the olmap plugin
		HttpResponse response = app
				.doGet("jslib/OpenLayers/OpenLayers.debug.js");
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine()
				.getStatusCode());
	}
}
