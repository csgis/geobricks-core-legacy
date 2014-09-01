package de.csgis.geobricks.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.junit.Test;

import de.csgis.geobricks.CustomConfigurator;
import de.csgis.geobricks.PluginDescriptor;

public class PluginListenerTest {
	@Test
	public void testInvalidPluginConf() {
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		PluginDescriptor descriptor = new PluginDescriptor();
		JSONObject conf = JSONObject.fromObject("{}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(0, descriptor.getDependencies().size());
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
		assertEquals(0, descriptor.getDependencies().size());
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
		assertEquals(0, descriptor.getDependencies().size());
		assertEquals(0, configurators.size());

		JSONObject defaultConf = descriptor.getDefaultConfiguration();
		assertTrue(defaultConf.getJSONObject("module").getBoolean("enabled"));
	}

	@Test
	public void dependencies() {
		String name = "openlayers";
		String path = "jslib/OpenLayers";

		PluginDescriptor descriptor = new PluginDescriptor();
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock',"
				+ "'non-require-deps' : { '" + name + "' : '" + path + "' }}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(1, descriptor.getDependencies().size());
		assertEquals(0, configurators.size());
		assertNull(descriptor.getDefaultConfiguration());

		assertEquals(path, descriptor.getDependencies().get(name));
	}

	@Test
	public void invalidConfiguratorClassName() {
		PluginDescriptor descriptor = new PluginDescriptor();
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		JSONObject conf = JSONObject.fromObject("{'id' : 'mock',"
				+ "'custom-configurator' : 'invalid_class'}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(0, descriptor.getDependencies().size());
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
		assertEquals(0, descriptor.getDependencies().size());
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

		assertEquals(0, descriptor.getDependencies().size());
		assertEquals(1, configurators.size());
		assertNull(descriptor.getDefaultConfiguration());

		assertEquals(MockConfigurator.class.getCanonicalName(), configurators
				.iterator().next().getClass().getCanonicalName());
	}

	@Test
	public void processCSSEntry() {
		PluginDescriptor descriptor = new PluginDescriptor();
		Set<String> entries = new HashSet<String>();
		entries.add(PluginListener.modulesDir + File.separator + "mock.css");

		PluginListener listener = new PluginListener();
		listener.processEntries(entries, descriptor);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(1, descriptor.getStyles().size());
		assertEquals("modules/mock.css", descriptor.getStyles().iterator()
				.next());
	}

	@Test
	public void processJSEntry() {
		PluginDescriptor descriptor = new PluginDescriptor();
		Set<String> entries = new HashSet<String>();
		entries.add(PluginListener.modulesDir + File.separator + "mock.js");

		PluginListener listener = new PluginListener();
		listener.processEntries(entries, descriptor);

		assertEquals(1, descriptor.getModules().size());
		assertEquals(0, descriptor.getStyles().size());
		assertEquals("mock", descriptor.getModules().iterator().next());
	}

	@Test
	public void processInvalidEntry() {
		PluginDescriptor descriptor = new PluginDescriptor();
		Set<String> entries = new HashSet<String>();
		entries.add("invalid_entry");

		PluginListener listener = new PluginListener();
		listener.processEntries(entries, descriptor);

		assertEquals(0, descriptor.getModules().size());
		assertEquals(0, descriptor.getStyles().size());
	}

	@Test
	public void processPluginId() {
		String id = "mock";

		PluginDescriptor descriptor = new PluginDescriptor();
		Set<CustomConfigurator> configurators = new HashSet<CustomConfigurator>();
		JSONObject conf = JSONObject.fromObject("{'id' : '" + id + "'}");

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor, configurators);
		assertEquals(id, descriptor.getId());
	}

	@Test
	public void css() {
		String[] css1 = new String[] { "a.css" };
		String[] css2 = new String[] { "b.css", "c.css" };

		PluginDescriptor descriptor = new PluginDescriptor();
		Collections.addAll(descriptor.getStyles(), css1);

		JSONObject conf = new JSONObject();
		conf.element("id", "mock");
		conf.element("css", css2);

		PluginListener listener = new PluginListener();
		listener.processPluginConf(conf, descriptor,
				new HashSet<CustomConfigurator>());

		assertArrayEquals(css2, descriptor.getStyles().toArray(new String[0]));
	}

	public static class MockConfigurator implements CustomConfigurator {
		@Override
		public void config(HttpServletRequest request,
				HttpServletResponse response, JSONObject staticConfig,
				String confDir) {
		}
	}
}
