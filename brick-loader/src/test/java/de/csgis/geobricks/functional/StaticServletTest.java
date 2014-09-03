package de.csgis.geobricks.functional;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.junit.Test;

public class StaticServletTest extends AbstractFunctionalTest {
	@Test
	public void getImage() throws Exception {
		// This image is on the ui plugin
		String img = "img.jpg";

		File original = File.createTempFile("close", ".png");
		InputStream originalStream = getClass().getResourceAsStream(
				"/webapp/images/" + img);
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
		HttpResponse response = app.doGet("jslib/mylib.js");
		assertEquals(HttpServletResponse.SC_OK, response.getStatusLine()
				.getStatusCode());
	}

	@Test
	public void getNonExistingResource() throws Exception {
		HttpResponse response = app.doGet("jslib/non_existing");
		assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatusLine()
				.getStatusCode());
	}

}
