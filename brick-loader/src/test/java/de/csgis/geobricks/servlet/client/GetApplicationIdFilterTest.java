package de.csgis.geobricks.servlet.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.Geobricks;

/**
 * Just check the JEE part, that the id is on the request. The logic is tested
 * elsewhere. I wouldn't test this, but thus we have a template. Remove if there
 * is more JEE tests
 * 
 * @author fergonco
 */
public class GetApplicationIdFilterTest {

	private ServletResponse response = mock(ServletResponse.class);
	private FilterChain chain = mock(FilterChain.class);
	private GetApplicationIdFilter filter;
	private ServletContext servletContext;
	private ServletRequest request;
	private FilterConfig filterConfig;

	@Before
	public void before() {
		filter = new GetApplicationIdFilter();
		servletContext = mock(ServletContext.class);
		request = mock(ServletRequest.class);
		when(request.getServletContext()).thenReturn(servletContext);
		filterConfig = mock(FilterConfig.class);
		when(filterConfig.getServletContext()).thenReturn(servletContext);
	}

	@Test
	public void testConfiguredId() throws Exception {
		String configuredid = "configuredid";
		when(servletContext.getInitParameter("geobricks-app-id")).thenReturn(
				configuredid);
		when(servletContext.getContextPath()).thenReturn("/myapp");

		filter.init(filterConfig);
		filter.doFilter(request, response, chain);

		verify(request).setAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE,
				configuredid);
	}
}
