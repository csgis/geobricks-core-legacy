package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class HTTPCodeServletExceptionTest {
	@Test
	public void noContent() {
		HTTPCodeServletException exc = new HTTPCodeServletException(204);
		assertEquals(204, exc.getStatusCode());
	}

	@Test
	public void noContentWithExceptionAndMessage() {
		try {
			new HTTPCodeServletException("Message", new IOException(), 204);
			fail();
		} catch (IllegalArgumentException e) {
			// do nothing
		}
	}

	@Test
	public void noContentExceptionMessage() {
		try {
			new HTTPCodeServletException(new IOException("Message"), 204);
			fail();
		} catch (IllegalArgumentException e) {
			// do nothing
		}
	}

}
