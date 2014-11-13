package de.csgis.geobricks.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.junit.Test;

import de.csgis.geobricks.CustomConfigurator;
import de.csgis.geobricks.PluginDescriptor;

public class PluginListenerTest {
	@Test
	public void invalidPluginConf() {
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		PluginDescriptor descriptor = new PluginDescriptor();
		JSONObject conf = JSONObject.fromObject("{}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(0, descriptor.getRequirePaths().size());
		assertNull(descriptor.getDefaultConfiguration());
		assertEquals(0, configurators.size());
	}

	@Test
	public void emptyPluginConf() {
		PluginDescriptor descriptor = new PluginDescriptor();
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock'}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(0, descriptor.getRequirePaths().size());
		assertNull(descriptor.getDefaultConfiguration());
		assertEquals(0, configurators.size());
	}

	@Test
	public void defaultConf() {
		String id = "mock";

		PluginDescriptor descriptor = new PluginDescriptor();
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		JSONObject conf = JSONObject.fromObject("{'id' : '" + id + "',"
				+ "'default-conf' : { 'module' : {'enabled' : true }}}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(0, descriptor.getRequirePaths().size());
		assertEquals(0, configurators.size());

		JSONObject defaultConf = descriptor.getDefaultConfiguration();
		assertTrue(defaultConf.getJSONObject("module").getBoolean("enabled"));
	}

	@Test
	public void requirePaths() {
		String name = "openlayers";
		String path = "jslib/OpenLayers";

		PluginDescriptor descriptor = new PluginDescriptor();
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock',"
				+ "'requirejs' : { 'paths' : { '" + name + "' : '" + path
				+ "' }}}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(1, descriptor.getRequirePaths().size());
		assertEquals(0, configurators.size());
		assertNull(descriptor.getDefaultConfiguration());

		assertEquals(path, descriptor.getRequirePaths().get(name));
	}

	@Test
	public void requireShim() {
		String shimLib = "openlayers";
		String shimDep = "jquery";

		PluginDescriptor descriptor = new PluginDescriptor();
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock',"
				+ "'requirejs' : { " //
				+ "'shim' : { " //
				+ "'" + shimLib + "' : ['" + shimDep + "'] "//
				+ "}}}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(1, descriptor.getRequireShim().size());
		assertEquals(0, configurators.size());
		assertNull(descriptor.getDefaultConfiguration());

		assertArrayEquals(new String[] { shimDep }, descriptor.getRequireShim()
				.get(shimLib));
	}

	@Test
	public void invalidConfiguratorClassName() {
		PluginDescriptor descriptor = new PluginDescriptor();
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock',"
				+ "'custom-configurator' : 'invalid_class'}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(0, descriptor.getRequirePaths().size());
		assertEquals(0, configurators.size());
		assertNull(descriptor.getDefaultConfiguration());
	}

	@Test
	public void invalidConfiguratorClass() {
		PluginDescriptor descriptor = new PluginDescriptor();
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock',"
				+ "'custom-configurator' : '"
				+ CustomConfigurator.class.getCanonicalName() + "'}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(0, descriptor.getRequirePaths().size());
		assertEquals(0, configurators.size());
		assertNull(descriptor.getDefaultConfiguration());
	}

	@Test
	public void validConfigurator() {
		String className = getClass().getCanonicalName() + "$MockConfigurator";

		PluginDescriptor descriptor = new PluginDescriptor();
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock',"
				+ "'custom-configurator' : '" + className + "'}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);

		assertEquals(0, descriptor.getRequirePaths().size());
		assertEquals(1, configurators.size());
		assertNull(descriptor.getDefaultConfiguration());

		assertEquals(MockConfigurator.class.getCanonicalName(), configurators
				.iterator().next().getClass().getCanonicalName());
	}

	@Test
	public void processCSSEntry() {
		PluginDescriptor descriptor = new PluginDescriptor();
		String entry = PluginListener.MODULES_PATH + File.separator
				+ "mock.css";

		PluginListener listener = new PluginListener();
		listener.processEntry(entry, descriptor);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(1, descriptor.getStyles().size());
		assertEquals("modules/mock.css", descriptor.getStyles().iterator()
				.next());
	}

	@Test
	public void processCSSFromStylesDirectory() {
		PluginDescriptor descriptor = new PluginDescriptor();
		String entry = PluginListener.STYLES_PATH + File.separator + "mock.css";

		PluginListener listener = new PluginListener();
		listener.processEntry(entry, descriptor);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(1, descriptor.getStyles().size());
		assertEquals("styles/mock.css", descriptor.getStyles().iterator()
				.next());
	}

	@Test
	public void processJSEntry() {
		PluginDescriptor descriptor = new PluginDescriptor();
		String entry = PluginListener.MODULES_PATH + File.separator + "mock.js";

		PluginListener listener = new PluginListener();
		listener.processEntry(entry, descriptor);

		assertEquals(1, descriptor.getModules().size());
		assertEquals(0, descriptor.getStyles().size());
		assertEquals("mock", descriptor.getModules().iterator().next());
	}

	@Test
	public void processInvalidEntry() {
		PluginDescriptor descriptor = new PluginDescriptor();

		PluginListener listener = new PluginListener();
		listener.processEntry("invalid_entry", descriptor);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(0, descriptor.getStyles().size());
	}

	@Test
	public void processEntriesFromJar() throws IOException {
		ZipInputStream jar = new ZipInputStream(getClass().getResourceAsStream(
				"/resources.jar"));
		PluginListener listener = new PluginListener();
		PluginDescriptor descriptor = listener.getModulesAndStylesFromJar(jar);

		List<String> styles = descriptor.getStyles();
		assertEquals(1, styles.size());
		assertEquals("modules/mock.css", styles.get(0));
	}

	@Test
	public void getPluginDescriptorFromJar() throws Exception {
		InputStream stream = mock(InputStream.class);
		ServletContext context = mock(ServletContext.class);
		URL pluginConf = new URL("jar:file:"
				+ getClass().getResource("/resources.jar").getFile()
				+ "!/conf/mock-conf.json");
		when(context.getResourceAsStream(anyString())).thenReturn(stream);

		PluginListener listener = spy(new PluginListener());
		doReturn(new PluginDescriptor()).when(listener)
				.getModulesAndStylesFromJar(any(ZipInputStream.class));
		listener.getModulesAndStyles(context, pluginConf);

		verify(listener).getModulesAndStylesFromJar(any(ZipInputStream.class));
	}

	public static class MockConfigurator implements CustomConfigurator {
		@Override
		public void config(HttpServletRequest request,
				HttpServletResponse response, JSONObject staticConfig,
				String confDir) {
		}

		@Override
		public Map<String, Class<? extends Filter>> getFilters() {
			return null;
		}
	}
}
