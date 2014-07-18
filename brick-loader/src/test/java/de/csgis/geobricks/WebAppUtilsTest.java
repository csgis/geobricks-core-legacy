package de.csgis.geobricks;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

public class WebAppUtilsTest {

	private ServletContext servletContext;

	@Before
	public void before() {
		servletContext = mock(ServletContext.class);
	}

	@Test
	public void testGetConfiguredId() throws Exception {
		String configuredId = "configuredid";
		when(servletContext.getInitParameter("geobricks-app-id")).thenReturn(
				configuredId);
		when(servletContext.getContextPath()).thenReturn("/myapp");

		String id = WebAppUtils.getApplicationId(servletContext);

		assertEquals(id, configuredId);
	}

	@Test
	public void testGetIdFromURL() throws Exception {
		when(servletContext.getInitParameter("geobricks-app-id")).thenReturn(
				null);
		String urlId = "myapp";
		when(servletContext.getContextPath()).thenReturn("/" + urlId);

		String id = WebAppUtils.getApplicationId(servletContext);

		assertEquals(id, urlId);
	}

}
