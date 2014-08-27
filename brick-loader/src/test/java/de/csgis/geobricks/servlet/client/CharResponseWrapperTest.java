package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class CharResponseWrapperTest {

	@Test
	public void getWriter() {
		CharResponseWrapper wrapper = new CharResponseWrapper(
				mock(HttpServletResponse.class));
		String s = "Testing wrapper";
		wrapper.getWriter().write(s);

		assertEquals(s, wrapper.toString());
	}

	@Test
	public void getMultipleWriters() {
		CharResponseWrapper wrapper = new CharResponseWrapper(
				mock(HttpServletResponse.class));
		String s = "Testing wrapper";
		wrapper.getWriter().write(s);
		wrapper.getWriter().write(s);

		assertEquals(s + s, wrapper.toString());
	}

}
