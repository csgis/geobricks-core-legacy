package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import de.csgis.geobricks.PluginDescriptor;

public class PluginDescriptorReaderTest {
	@Test
	public void invalidPluginConf() {
		PluginDescriptor descriptor = new PluginDescriptor();
		JSONObject conf = JSONObject.fromObject("{}");

		PluginDescriptorReader reader = new PluginDescriptorReader();
		reader.processPluginConf(conf, descriptor);
		assertEquals(0, descriptor.getRequirePaths().size());
		assertNull(descriptor.getDefaultConfiguration());
	}

	@Test
	public void emptyPluginConf() {
		PluginDescriptor descriptor = new PluginDescriptor();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock'}");

		PluginDescriptorReader reader = new PluginDescriptorReader();
		reader.processPluginConf(conf, descriptor);
		assertEquals(0, descriptor.getRequirePaths().size());
		assertNull(descriptor.getDefaultConfiguration());
	}

	@Test
	public void defaultConf() {
		String id = "mock";

		PluginDescriptor descriptor = new PluginDescriptor();
		JSONObject conf = JSONObject.fromObject("{'id' : '" + id + "',"
				+ "'default-conf' : { 'module' : {'enabled' : true }}}");

		PluginDescriptorReader reader = new PluginDescriptorReader();
		reader.processPluginConf(conf, descriptor);
		assertEquals(0, descriptor.getRequirePaths().size());

		JSONObject defaultConf = descriptor.getDefaultConfiguration();
		assertTrue(defaultConf.getJSONObject("module").getBoolean("enabled"));
	}

	@Test
	public void requirePaths() {
		String name = "openlayers";
		String path = "jslib/OpenLayers";

		PluginDescriptor descriptor = new PluginDescriptor();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock',"
				+ "'requirejs' : { 'paths' : { '" + name + "' : '" + path
				+ "' }}}");

		PluginDescriptorReader reader = new PluginDescriptorReader();
		reader.processPluginConf(conf, descriptor);
		assertEquals(1, descriptor.getRequirePaths().size());
		assertNull(descriptor.getDefaultConfiguration());

		assertEquals(path, descriptor.getRequirePaths().get(name));
	}

	@Test
	public void requireShim() {
		String shimLib = "openlayers";
		String shimDep = "jquery";

		PluginDescriptor descriptor = new PluginDescriptor();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock',"
				+ "'requirejs' : { " //
				+ "'shim' : { " //
				+ "'" + shimLib + "' : ['" + shimDep + "'] "//
				+ "}}}");

		PluginDescriptorReader reader = new PluginDescriptorReader();
		reader.processPluginConf(conf, descriptor);
		assertEquals(1, descriptor.getRequireShim().size());
		assertNull(descriptor.getDefaultConfiguration());

		JSONArray array = (JSONArray) descriptor.getRequireShim().get(shimLib);
		assertArrayEquals(new String[] { shimDep }, array.toArray());
	}

	@Test
	public void processCSSEntry() {
		PluginDescriptor descriptor = new PluginDescriptor();
		String entry = PluginDescriptorReader.MODULES_PATH + File.separator
				+ "mock.css";

		PluginDescriptorReader reader = new PluginDescriptorReader();
		reader.processCSSEntry(entry, descriptor);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(1, descriptor.getStyles().size());
		assertEquals("modules/mock.css", descriptor.getStyles().iterator()
				.next());
	}

	@Test
	public void processCSSFromStylesDirectory() {
		PluginDescriptor descriptor = new PluginDescriptor();
		String entry = PluginDescriptorReader.STYLES_PATH + File.separator
				+ "mock.css";

		PluginDescriptorReader reader = new PluginDescriptorReader();
		reader.processCSSEntry(entry, descriptor);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(1, descriptor.getStyles().size());
		assertEquals("styles/mock.css", descriptor.getStyles().iterator()
				.next());
	}

	@Test
	public void processCSSFromThemeDirectory() {
		PluginDescriptor descriptor = new PluginDescriptor();
		String entry = PluginDescriptorReader.THEME_PATH + File.separator
				+ "mock.css";

		PluginDescriptorReader reader = new PluginDescriptorReader();
		reader.processCSSEntry(entry, descriptor);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(1, descriptor.getStyles().size());
		assertEquals("theme/mock.css", descriptor.getStyles().iterator().next());
	}

	@Test
	public void processJSEntry() {
		PluginDescriptor descriptor = new PluginDescriptor();
		String entry = PluginDescriptorReader.MODULES_PATH + File.separator
				+ "mock.js";

		PluginDescriptorReader reader = new PluginDescriptorReader();
		reader.processJSEntry(entry, descriptor);

		assertEquals(1, descriptor.getModules().size());
		assertEquals(0, descriptor.getStyles().size());
		assertEquals("mock", descriptor.getModules().iterator().next());
	}

	@Test
	public void processInvalidEntry() {
		PluginDescriptor descriptor = new PluginDescriptor();

		PluginDescriptorReader reader = new PluginDescriptorReader();
		reader.processJSEntry("invalid_entry", descriptor);
		reader.processCSSEntry("invalid_entry", descriptor);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(0, descriptor.getStyles().size());
	}

	@Test
	public void processEntriesFromJar() throws IOException {
		String file = getClass().getResource("/resources.jar").getPath();
		PluginDescriptorReader reader = new PluginDescriptorReader();
		PluginDescriptor descriptor = reader.getModulesAndStylesFromJar(file);

		List<String> styles = descriptor.getStyles();
		assertEquals(1, styles.size());
		assertEquals("modules/mock.css", styles.get(0));
	}

	@Test
	public void dontProcessJSEntryFromStyles() {
		PluginDescriptor descriptor = new PluginDescriptor();
		String entry = PluginDescriptorReader.STYLES_PATH + File.separator
				+ "mock.js";

		PluginDescriptorReader listener = new PluginDescriptorReader();
		listener.processJSEntry(entry, descriptor);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(0, descriptor.getStyles().size());
	}

	@Test
	public void processDirectory() {
		File root = new File(getClass().getResource("/").getPath());

		PluginDescriptorReader reader = new PluginDescriptorReader();
		PluginDescriptor descriptor = reader.getModulesAndStylesFromDir(root);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(3, descriptor.getStyles().size());
	}

	@Test
	public void getPluginDescriptorFromJar() throws Exception {
		URL pluginConf = new URL("jar:file:"
				+ getClass().getResource("/resources.jar").getFile()
				+ "!/conf/mock-conf.json");

		PluginDescriptorReader reader = spy(new PluginDescriptorReader());
		doReturn(new PluginDescriptor()).when(reader)
				.getModulesAndStylesFromJar(any(String.class));
		reader.getModulesAndStyles(pluginConf);

		verify(reader).getModulesAndStylesFromJar(any(String.class));
	}
}
