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
	private static final String MAIN = "/webapp/modules/main.js";

	private MainModuleContentProcessor processor;

	@Before
	public void setup() {
		processor = new MainModuleContentProcessor();
	}

	@Test
	public void replaceRequireDeps() throws IOException {
		String name = "dependency";
		String path = "jslib/OpenLayers";
		String content = IOUtils.toString(getClass().getResourceAsStream(MAIN));

		PluginDescriptor descriptor = new PluginDescriptor();
		descriptor.getRequirePaths().put(name, path);
		String processed = processor.process(content,
				new PluginDescriptor[] { descriptor });

		Pattern pattern = Pattern.compile("paths\\s*:\\s*\\{.*\"" + name
				+ "\"\\s*:\\s*\"../" + path + "\"", Pattern.DOTALL);
		assertTrue(content.contains("$paths"));
		assertFalse(processed.contains("$paths"));
		assertTrue(pattern.matcher(processed).find());
	}

	@Test
	public void noRequireDeps() throws Exception {
		String content = IOUtils.toString(getClass().getResourceAsStream(MAIN));

		PluginDescriptor descriptor = new PluginDescriptor();
		String processed = processor.process(content,
				new PluginDescriptor[] { descriptor });

		// Only jquery dependency
		Pattern pattern = Pattern.compile(
				"paths\\s*:\\s*\\{[^\\}]*\"jquery\"\\s*:\\s*[^\\}]+\\}",
				Pattern.DOTALL);
		assertTrue(content.contains("$paths"));
		assertFalse(processed.contains("$paths"));
		assertTrue(pattern.matcher(processed).find());
	}

	@Test
	public void replaceRequireShim() throws IOException {
		String shimLib = "dependency";
		String shimDep = "openlayers";
		String content = IOUtils.toString(getClass().getResourceAsStream(MAIN));

		PluginDescriptor descriptor = new PluginDescriptor();
		descriptor.getRequireShim().put(shimLib, new String[] { shimDep });
		String processed = processor.process(content,
				new PluginDescriptor[] { descriptor });

		Pattern pattern = Pattern.compile("shim\\s*:\\s*\\{.*\"" + shimLib
				+ "\"\\s*:\\s*\\[\"" + shimDep + "\"\\]", Pattern.DOTALL);
		assertTrue(content.contains("$shim"));
		assertFalse(processed.contains("$shim"));
		assertTrue(pattern.matcher(processed).find());
	}

	@Test
	public void noRequireShim() throws Exception {
		String content = IOUtils.toString(getClass().getResourceAsStream(MAIN));

		PluginDescriptor descriptor = new PluginDescriptor();
		String processed = processor.process(content,
				new PluginDescriptor[] { descriptor });

		// Empty shim
		Pattern pattern = Pattern.compile("shim\\s*:\\s*\\{\\s*\\}",
				Pattern.DOTALL);

		assertTrue(content.contains("$shim"));
		assertFalse(processed.contains("$shim"));
		assertTrue(pattern.matcher(processed).find());
	}
}
