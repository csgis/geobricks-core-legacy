package de.csgis.geobricks.servlet;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import de.csgis.geobricks.servlet.IndexHTMLRedirectFilter;

public class IndexHTMLRedirectFilterTest {

	@Test
	public void redirectNotEndingSlash() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getRequestURI()).thenReturn("myapp");

		new IndexHTMLRedirectFilter().doFilter(request, response, chain);

		verify(response).sendRedirect("myapp/");
	}

	@Test
	public void notRedirectEndingSlash() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getRequestURI()).thenReturn("myapp/");

		new IndexHTMLRedirectFilter().doFilter(request, response, chain);

		verify(response, never()).sendRedirect(anyString());
	}
}
