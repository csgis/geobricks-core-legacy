package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.csgis.geobricks.servlet.ClasspathResourceServlet;

public class ClasspathResourceServletTest {
	@Test
	public void nullURIForFixedResource() throws Exception {
		ClasspathResourceServlet servlet = new ClasspathResourceServlet(null,
				"index.html");
		try {
			servlet.getResourceAsStream(null);
			fail();
		} catch (IOException e) {
		}

		assertEquals("index.html", servlet.getResource(null));
	}

	@Test
	public void validURIForFixedResource() throws Exception {
		ClasspathResourceServlet servlet = new ClasspathResourceServlet(null,
				"index.html");

		String uri = "index.html";
		assertEquals(uri, servlet.getResource(uri));
		InputStream resource = servlet.getResourceAsStream(uri);

		StringWriter writer = new StringWriter();
		IOUtils.copy(resource, writer);
		String content = writer.toString();

		assertTrue(content.contains("<html>"));
	}

	@Test
	public void folderResource() throws Exception {
		ClasspathResourceServlet servlet = new ClasspathResourceServlet(
				"modules");

		String uri = "modules/main.js";
		assertEquals(uri, servlet.getResource(uri));
		InputStream resource = servlet.getResourceAsStream(uri);

		StringWriter writer = new StringWriter();
		IOUtils.copy(resource, writer);
		String content = writer.toString();

		assertTrue(content.contains("require.config"));
	}

	@Test
	public void nonExistingResource() throws Exception {
		ClasspathResourceServlet servlet = new ClasspathResourceServlet(
				"modules");
		String uri = "modules/non_existing_resource";
		try {
			servlet.getResourceAsStream(uri);
			fail();
		} catch (IOException e) {
			// do nothing
		}

		assertEquals(uri, servlet.getResource(uri));
	}

	@Test
	public void resourceOutsideFolder() throws Exception {
		ClasspathResourceServlet servlet = new ClasspathResourceServlet(
				"modules");
		try {
			servlet.getResourceAsStream("index.html");
			fail();
		} catch (IOException e) {
			// do nothing
		}

		try {
			servlet.getResource("index.html");
			fail();
		} catch (IOException e) {
			// do nothing
		}
	}

	@Test
	public void rootSetsContentTypeAndEncoding() throws Exception {
		ClasspathResourceServlet servlet = new ClasspathResourceServlet("",
				"index.html");

		HttpServletResponse resp = mock(HttpServletResponse.class);
		servlet.setContentTypeAndEncoding(resp,
				servlet.getResource("myviewer-1.0-SNAPSHOT/"));

		verify(resp).setContentType("text/html");
		verify(resp).setCharacterEncoding("UTF-8");
	}

	@Test
	public void cssContentTypeAndEncoding() throws Exception {
		ClasspathResourceServlet servlet = new ClasspathResourceServlet("",
				"index.css");

		HttpServletResponse resp = mock(HttpServletResponse.class);
		servlet.setContentTypeAndEncoding(resp,
				servlet.getResource("myviewer-1.0-SNAPSHOT/"));

		verify(resp).setContentType("text/css");
		verify(resp).setCharacterEncoding("UTF-8");
	}

	@Test
	public void jsContentTypeAndEncoding() throws Exception {
		ClasspathResourceServlet servlet = new ClasspathResourceServlet("",
				"index.js");

		HttpServletResponse resp = mock(HttpServletResponse.class);
		servlet.setContentTypeAndEncoding(resp,
				servlet.getResource("myviewer-1.0-SNAPSHOT/"));

		verify(resp).setContentType("application/javascript");
		verify(resp).setCharacterEncoding("UTF-8");
	}

	@Test
	public void svgContentTypeAndEncoding() throws Exception {
		ClasspathResourceServlet servlet = new ClasspathResourceServlet(
				"images");

		HttpServletResponse resp = mock(HttpServletResponse.class);
		servlet.setContentTypeAndEncoding(resp,
				servlet.getResource("images/img.svg"));

		verify(resp).setContentType("image/svg+xml");
		verify(resp).setCharacterEncoding("UTF-8");
	}
}
