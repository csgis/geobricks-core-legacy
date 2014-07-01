package de.csgis.geobricks.servlet.client;

import static org.mockito.Mockito.atLeastOnce;
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

	@Test
	public void testFromURLId() throws Exception {
		when(servletContext.getInitParameter("geobricks-app-id")).thenReturn(
				null);
		when(servletContext.getContextPath()).thenReturn("/myapp");

		filter.init(filterConfig);
		filter.doFilter(request, response, chain);

		verify(request).setAttribute(Geobricks.APP_ID_HTTP_ATTRIBUTE, "myapp");
	}

	/*
	 * In GBAppRunner only one filter is used, so it has to work with two
	 * ServletContexts
	 */
	@Test
	public void testTwoAppsFromURLId() throws Exception {
		when(servletContext.getInitParameter("geobricks-app-id")).thenReturn(
				null);
		String firstApp = "firstapp";
		String secondApp = "secondapp";
		when(servletContext.getContextPath()).thenReturn("/" + firstApp)
				.thenReturn("/" + secondApp);

		filter.init(filterConfig);
		filter.doFilter(request, response, chain);
		filter.doFilter(request, response, chain);

		verify(request, atLeastOnce()).setAttribute(
				Geobricks.APP_ID_HTTP_ATTRIBUTE, firstApp);
		verify(request, atLeastOnce()).setAttribute(
				Geobricks.APP_ID_HTTP_ATTRIBUTE, secondApp);
	}
}
