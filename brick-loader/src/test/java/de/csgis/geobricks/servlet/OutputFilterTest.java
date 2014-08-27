package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

public class OutputFilterTest {
	private OutputFilter filter;

	@Before
	public void setup() {
		filter = new OutputFilter();
	}

	@Test
	public void statusCode() throws Exception {
		HttpServletResponse response = mock(HttpServletResponse.class);
		filter.manageException(new HTTPCodeServletException(204), response);
		verify(response).setStatus(eq(204));
	}

	@Test
	public void errorMessage() throws Exception {
		StringWriter writer = new StringWriter();
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(writer));

		String message = "All is correct.";
		filter.manageException(new HTTPCodeServletException(message, 200),
				response);
		JSONObject json = JSONObject.fromObject(writer.toString());
		assertEquals(message, json.getString("message"));
	}

	@Test
	public void nullMessage() throws IOException {
		StringWriter writer = new StringWriter();
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(writer));

		filter.manageException(
				new HTTPCodeServletException((String) null, 200), response);
		assertEquals("", writer.toString());
	}

	@Test
	public void unknownException() throws IOException {
		StringWriter writer = new StringWriter();
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(writer));

		filter.manageException(new IOException(), response);
		verify(response).setStatus(eq(500));
	}
}
