package de.csgis.geobricks.functional;

import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApplicationManagementTest {

	private static Server server;

	@BeforeClass
	public static void start() throws Exception {
		server = new Server(9090);
		server.setStopAtShutdown(true);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/app");
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
	public void testGetIndex() throws Exception {
		URL url = new URL("http://localhost:9090/app/index.html");
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		int code = http.getResponseCode();
		Assert.assertTrue(code == 200);
	}

}
