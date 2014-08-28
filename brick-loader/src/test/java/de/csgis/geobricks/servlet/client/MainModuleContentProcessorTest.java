package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.csgis.geobricks.NonRequireDependency;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.guice.RuntimeModule;

public class MainModuleContentProcessorTest {
	private Injector injector;
	private MainModuleContentProcessor processor;
	private PluginRegistry pluginRegistry;

	@Before
	public void setup() {
		injector = Guice.createInjector(new RuntimeModule());
		processor = injector.getInstance(MainModuleContentProcessor.class);
		pluginRegistry = injector.getInstance(PluginRegistry.class);
	}

	@Test
	public void replaceNonRequireJSDependencies() throws IOException {
		NonRequireDependency[] deps = new NonRequireDependency[] { new NonRequireDependency(
				"dependency", "jslib/OpenLayers") };
		PluginDescriptor plugin = mock(PluginDescriptor.class);
		when(plugin.getNonRequireDependencies()).thenReturn(deps);
		pluginRegistry.putPlugin(plugin);

		String content = IOUtils.toString(getClass().getResourceAsStream(
				"/de/csgis/geobricks/webapp/modules/main.js"));
		String processed = processor.process(content);

		String regex = "paths\\s*:\\s*\\{.*\"dependency\"\\s*:\\s*\"../jslib/OpenLayers\"";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		assertTrue(content.contains("$nonRequireJSDependencies"));
		assertFalse(processed.contains("$nonRequireJSDependencies"));
		assertTrue(pattern.matcher(processed).find());
	}
}
