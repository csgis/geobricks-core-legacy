package de.csgis.geobricks.guice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.inject.Injector;

public class GuiceServletConfigTest {
	@Test
	public void validModuleClass() {
		ServletContext context = mock(ServletContext.class);
		when(context.getInitParameter("guice-module-class")).thenReturn(
				RuntimeModule.class.getCanonicalName());

		ServletContextEvent event = mock(ServletContextEvent.class);
		when(event.getServletContext()).thenReturn(context);

		GuiceServletConfig config = new GuiceServletConfig();
		config.contextInitialized(event);

		ArgumentCaptor<Injector> captor = ArgumentCaptor
				.forClass(Injector.class);
		verify(context).setAttribute(eq(Injector.class.getCanonicalName()),
				captor.capture());

		assertEquals(config.getInjector(), captor.getValue());
	}

	@Test
	public void nonExistingModuleClass() {
		ServletContext context = mock(ServletContext.class);
		when(context.getInitParameter("guice-module-class")).thenReturn(
				"invalid_class");

		ServletContextEvent event = mock(ServletContextEvent.class);
		when(event.getServletContext()).thenReturn(context);

		try {
			new GuiceServletConfig().contextInitialized(event);
			fail();
		} catch (RuntimeException e) {
			// do nothing
		}
	}
}
