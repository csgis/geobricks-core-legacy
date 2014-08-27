package de.csgis.geobricks.guice;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;

import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.functional.MockPlugin;

public class PluginListenerTest {
	private PluginRegistry registry;
	private ServletContext context;
	private PluginListener listener;

	@Before
	public void setup() {
		context = mock(ServletContext.class);
		listener = new PluginListener();
		registry = new PluginRegistry();

		Injector injector = mock(Injector.class);
		when(injector.getInstance(PluginRegistry.class)).thenReturn(registry);
		when(injector.getInstance(MockPlugin.class)).thenReturn(
				new MockPlugin());

		when(context.getAttribute(Injector.class.getCanonicalName()))
				.thenReturn(injector);
	}

	@Test
	public void mockPlugin() {
		mockInitParams(MockPlugin.ID, MockPlugin.class.getCanonicalName());

		listener.configurePluginRegistry(context);

		assertEquals(1, registry.getPlugins().size());
		assertArrayEquals(new MockPlugin().getStyleSheets(), registry
				.getPlugin(MockPlugin.ID).getStyleSheets());
	}

	@Test
	public void noPlugins() {
		when(context.getInitParameterNames()).thenReturn(
				new Vector<String>().elements());
		listener.configurePluginRegistry(context);
		assertEquals(0, registry.getPlugins().size());
	}

	@Test
	public void invalidClassName() {
		mockInitParams(MockPlugin.ID, "invalid_class_name");

		listener.configurePluginRegistry(context);

		assertEquals(0, registry.getPlugins().size());
	}

	private void mockInitParams(String id, String clazz) {
		String initParam = PluginListener.CONTEXT_PARAM_PREFIX + id;
		Set<String> names = Collections.singleton(initParam);
		when(context.getInitParameterNames()).thenReturn(
				new Vector<String>(names).elements());
		when(context.getInitParameter(initParam)).thenReturn(clazz);
	}
}
