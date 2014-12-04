package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.csgis.geobricks.servlet.HTTPCodeServletException;

public class StaticServletTest {
	@Test
	public void fixedResource() throws Exception {
		StaticTextServlet servlet = new StaticTextServlet(null, "index.html");

		StringWriter writer = new StringWriter();
		// URI is ignored
		InputStream resource = servlet.getResourceStream(null);
		IOUtils.copy(resource, writer);
		String content = writer.toString();

		assertTrue(content.contains("<html>"));
	}

	@Test
	public void folderResource() throws Exception {
		StaticTextServlet servlet = new StaticTextServlet("modules");

		StringWriter writer = new StringWriter();
		// URI is ignored
		InputStream resource = servlet.getResourceStream("modules/main.js");
		IOUtils.copy(resource, writer);
		String content = writer.toString();

		assertTrue(content.contains("require.config"));
	}

	@Test
	public void nonExistingResource() {
		StaticTextServlet servlet = new StaticTextServlet("modules");

		try {
			servlet.getResourceStream("non_existing_resource");
			fail();
		} catch (HTTPCodeServletException e) {
			// do nothing
		}
	}

	@Test
	public void rootSetsContentTypeAndEncoding() {
		StaticTextServlet servlet = new StaticTextServlet("", "index.html");

		HttpServletResponse resp = mock(HttpServletResponse.class);
		servlet.setContentTypeAndEncoding(resp, "myviewer-1.0-SNAPSHOT/");

		verify(resp).setContentType("text/html");
		verify(resp).setCharacterEncoding("UTF-8");
	}
}
