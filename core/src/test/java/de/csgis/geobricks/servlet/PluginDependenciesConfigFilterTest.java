package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginDescriptorReader;

public class PluginDependenciesConfigFilterTest {
	private PluginDependenciesConfigFilter filter;
	private PluginDescriptorReader reader;

	@Before
	public void setup() {
		this.reader = mock(PluginDescriptorReader.class);
		this.filter = new PluginDependenciesConfigFilter(reader);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void addsDependenciesIfNotPresent() throws Exception {
		PluginDescriptor dependency = new PluginDescriptor("dependency");
		PluginDescriptor plugin = new PluginDescriptor("plugin");
		plugin.getDependencies().add(dependency);

		when(reader.getDescriptors(anyList())).thenReturn(
				new PluginDescriptor[] { plugin });

		JSONObject config = JSONObject.fromObject("{plugin : {}}");
		config = filter.modifyConfig(config, mock(HttpServletRequest.class),
				mock(HttpServletResponse.class));

		assertTrue(config.has("dependency"));
		assertEquals(new JSONObject(), config.getJSONObject("dependency"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void doesNotAddDependenciesIfAlreadyPresent() throws Exception {
		PluginDescriptor dependency = new PluginDescriptor("dependency");
		PluginDescriptor plugin = new PluginDescriptor("plugin");
		plugin.getDependencies().add(dependency);

		when(reader.getDescriptors(anyList())).thenReturn(
				new PluginDescriptor[] { plugin });

		JSONObject depConfig = JSONObject.fromObject("{module:{value : true}}");
		JSONObject config = JSONObject.fromObject("{plugin : {}, "
				+ "dependency : " + depConfig.toString() + "}");
		config = filter.modifyConfig(config, mock(HttpServletRequest.class),
				mock(HttpServletResponse.class));

		assertTrue(config.has("dependency"));
		assertEquals(depConfig, config.getJSONObject("dependency"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void addsDependenciesOnlyOnceIfRepeated() throws Exception {
		PluginDescriptor dependency = new PluginDescriptor("dependency");
		PluginDescriptor p1 = new PluginDescriptor("p1");
		PluginDescriptor p2 = new PluginDescriptor("p2");
		p1.getDependencies().add(dependency);
		p2.getDependencies().add(dependency);

		when(reader.getDescriptors(anyList())).thenReturn(
				new PluginDescriptor[] { p1, p2 });

		JSONObject config = JSONObject.fromObject("{p1 : {}, p2 : {}}");
		config = filter.modifyConfig(config, mock(HttpServletRequest.class),
				mock(HttpServletResponse.class));

		assertTrue(config.has("dependency"));
		assertEquals(new JSONObject(), config.getJSONObject("dependency"));
		assertEquals(StringUtils.countMatches(config.toString(), "dependency"),
				1);
	}
}
