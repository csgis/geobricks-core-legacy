package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.PluginDescriptor;

public class MainModuleContentProcessorTest {
	private MainModuleContentProcessor processor;

	@Before
	public void setup() {
		processor = new MainModuleContentProcessor();
	}

	@Test
	public void replaceNonRequireJSDependencies() throws IOException {
		String name = "dependency";
		String path = "jslib/OpenLayers";
		String content = IOUtils.toString(getClass().getResourceAsStream(
				"/de/csgis/geobricks/webapp/modules/main.js"));

		PluginDescriptor descriptor = new PluginDescriptor();
		descriptor.getDependencies().put(name, path);
		String processed = processor.process(content,
				new PluginDescriptor[] { descriptor });

		Pattern pattern = Pattern.compile("paths\\s*:\\s*\\{.*\"" + name
				+ "\"\\s*:\\s*\"../" + path + "\"", Pattern.DOTALL);
		assertTrue(content.contains("$nonRequireJSDependencies"));
		assertFalse(processed.contains("$nonRequireJSDependencies"));
		assertTrue(pattern.matcher(processed).find());
	}

	@Test
	public void noDependencies() throws Exception {
		String content = IOUtils.toString(getClass().getResourceAsStream(
				"/de/csgis/geobricks/webapp/modules/main.js"));

		PluginDescriptor descriptor = new PluginDescriptor();
		String processed = processor.process(content,
				new PluginDescriptor[] { descriptor });

		// Only jquery dependency
		Pattern pattern = Pattern.compile(
				"paths\\s*:\\s*\\{[^\\}]*\"jquery\"\\s*:\\s*[^\\}]+\\}",
				Pattern.DOTALL);
		assertTrue(content.contains("$nonRequireJSDependencies"));
		assertFalse(processed.contains("$nonRequireJSDependencies"));
		assertTrue(pattern.matcher(processed).find());
	}
}
