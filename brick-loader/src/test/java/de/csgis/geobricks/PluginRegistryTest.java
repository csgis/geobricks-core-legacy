package de.csgis.geobricks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PluginRegistryTest {
	private PluginRegistry registry;

	@Before
	public void setup() {
		registry = new PluginRegistry();
	}

	@Test
	public void getNonExistingPlugin() {
		assertNull(registry.getPlugin("non_existing"));
	}

	@Test
	public void putAndGetExistingPlugin() {
		String id = "myplugin";
		PluginDescriptor plugin = mock(PluginDescriptor.class);
		when(plugin.getId()).thenReturn(id);

		registry.putPlugin(plugin);

		assertEquals(plugin, registry.getPlugin(id));
	}

	@Test
	public void getNonRequireJSDependencies() {
		NonRequireDependency[] deps1 = new NonRequireDependency[] {
				new NonRequireDependency("one", "jslib/one"),
				new NonRequireDependency("two", "jslib/two") };
		NonRequireDependency[] deps2 = new NonRequireDependency[] {
				new NonRequireDependency("a", "jslib/a"),
				new NonRequireDependency("b", "jslib/b") };

		registry.putPlugin(mockPlugin("p1", deps1));
		registry.putPlugin(mockPlugin("p2", deps2));

		List<NonRequireDependency> depsList = Arrays.asList(registry
				.getNonRequireDependencies());

		// +1 because of jquery hardcoded
		assertEquals(deps1.length + deps2.length + 1,
				registry.getNonRequireDependencies().length);
		assertTrue(depsList.containsAll(Arrays.asList(deps1)));
		assertTrue(depsList.containsAll(Arrays.asList(deps2)));
	}

	private PluginDescriptor mockPlugin(String id, NonRequireDependency[] deps) {
		PluginDescriptor plugin = mock(PluginDescriptor.class);
		when(plugin.getId()).thenReturn(id);
		when(plugin.getNonRequireDependencies()).thenReturn(deps);
		return plugin;
	}
}
