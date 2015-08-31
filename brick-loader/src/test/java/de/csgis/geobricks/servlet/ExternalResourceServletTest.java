package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.servlet.ExternalResourceServlet;

public class ExternalResourceServletTest {
	private File dir;
	private ExternalResourceServlet servlet;

	@Before
	public void setup() throws Exception {
		dir = File.createTempFile("geobricks-", "");
		dir.delete();
		dir.mkdir();
		servlet = new ExternalResourceServlet(dir);
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(dir);
	}

	@Test
	public void nonExistingResource() throws Exception {
		try {
			servlet.getResourceAsStream("non_existing_file");
			fail();
		} catch (IOException e) {
		}
		try {
			servlet.getResource("non_existing_file");
			fail();
		} catch (IOException e) {
		}
	}

	@Test
	public void existingResource() throws Exception {
		String data = "data";
		File file = new File(dir, "file.txt");

		FileWriter output = new FileWriter(file);
		IOUtils.write(data, output);
		output.close();

		String resource = servlet.getResource("_static/file.txt");
		assertEquals(file.getAbsolutePath(), resource);
		InputStream stream = servlet.getResourceAsStream(resource);

		StringWriter writer = new StringWriter();
		IOUtils.copy(stream, writer);

		assertEquals(data, writer.toString());
	}

	@Test
	public void htmlContentTypeAndEncoding() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		servlet.setContentTypeAndEncoding(resp,
				servlet.getResource("_static/file.html"));
		verify(resp).setContentType("text/html");
		verify(resp).setCharacterEncoding("UTF-8");
	}

	@Test
	public void cssContentTypeAndEncoding() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		servlet.setContentTypeAndEncoding(resp,
				servlet.getResource("_static/index.css"));

		verify(resp).setContentType("text/css");
		verify(resp).setCharacterEncoding("UTF-8");
	}

	@Test
	public void jsContentTypeAndEncoding() throws Exception {
		HttpServletResponse resp = mock(HttpServletResponse.class);
		servlet.setContentTypeAndEncoding(resp,
				servlet.getResource("_static/index.js"));

		verify(resp).setContentType("application/javascript");
		verify(resp).setCharacterEncoding("UTF-8");
	}
}
