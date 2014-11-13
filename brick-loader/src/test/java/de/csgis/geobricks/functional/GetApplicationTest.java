package de.csgis.geobricks.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.junit.Test;

public class GetApplicationTest extends AbstractFunctionalTest {
	@Test
	public void indexHTML() throws Exception {
		checkIndexHtml(app.doGet("index.html"));
	}

	@Test
	public void appWithoutSlash() throws Exception {
		checkIndexHtml(app.doGet());
	}

	@Test
	public void appWithSlash() throws Exception {
		checkIndexHtml(app.doGet(""));
	}

	private void checkIndexHtml(HttpResponse response) throws Exception {
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine()
				.getStatusCode());

		String content = IOUtils.toString(response.getEntity().getContent());

		assertTrue(content.trim().startsWith("<!DOCTYPE html>"));
		assertTrue(content.contains("config.js"));
	}

	@Test
	public void libJQuery() throws Exception {
		String path = "jslib/jquery-1.11.0.min.js";
		HttpResponse response = app.doGet(path);

		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine()
				.getStatusCode());

		InputStream original = getClass()
				.getResourceAsStream("/webapp/" + path);
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
	public void moduleContentType() throws Exception {
		HttpResponse response = app.doGet("modules/main.js");
		Header contentType = response.getLastHeader("Content-Type");
		assertEquals("application/javascript; charset=UTF-8",
				contentType.getValue());
	}

	@Test
	public void indexContentType() throws Exception {
		HttpResponse response = app.doGet("index.html");
		Header contentType = response.getLastHeader("Content-Type");
		assertEquals("text/html; charset=UTF-8", contentType.getValue());
	}

	@Test
	public void indexHTMLContainsCSS() throws Exception {
		HttpResponse response = app.doGet();
		String content = IOUtils.toString(response.getEntity().getContent());
		Pattern pattern = Pattern.compile("<link[^>]*mock.css[^>]/>");
		assertTrue(pattern.matcher(content).find());
	}

	@Test
	public void indexHTMLNoBootstrapCSS() throws Exception {
		HttpResponse response = app.doGet();
		String content = IOUtils.toString(response.getEntity().getContent());
		Pattern pattern = Pattern.compile("<link[^>]*bootstrap.*[^>]/>");
		assertFalse(pattern.matcher(content).find());
	}

	@Test
	public void getConfig() throws Exception {
		HttpResponse response = app.doGet("config.js");
		String content = IOUtils.toString(response.getEntity().getContent());
		assertTrue(content.startsWith("var require = "));
		JSONObject json = JSONObject.fromObject(content
				.substring("var require = ".length()));
		assertTrue(json.getJSONObject("config").has("load-modules"));
	}
}
