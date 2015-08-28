package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.junit.Test;

import de.csgis.geobricks.PluginDescriptor;

public class PluginDefaultsConfigFilterTest {
	@Test
	public void addsDefaultConfig() throws Exception {
		String id = "plugin";

		PluginDescriptor p = new PluginDescriptor();
		p.setDefaultConfiguration(JSONObject.fromObject("{'a' : true }"));
		p.setId(id);

		PluginDefaultsConfigFilter filter = new PluginDefaultsConfigFilter(
				mockConfig(p));

		JSONObject jsonConfig = JSONObject.fromObject("{'" + id + "' : {}}");

		JSONObject modified = filter
				.modifyConfig(jsonConfig, mock(HttpServletRequest.class),
						mock(HttpServletResponse.class));

		assertTrue(modified.getJSONObject(id).getBoolean("a"));
	}

	@Test
	public void doesNotOverrideConfig() throws Exception {
		String id = "plugin";

		PluginDescriptor p = new PluginDescriptor();
		p.setDefaultConfiguration(JSONObject.fromObject("{'a' : true }"));
		p.setId(id);

		PluginDefaultsConfigFilter filter = new PluginDefaultsConfigFilter(
				mockConfig(p));

		JSONObject config = JSONObject.fromObject("{'" + id
				+ "' : { 'a' : false }}");

		JSONObject modified = filter
				.modifyConfig(config, mock(HttpServletRequest.class),
						mock(HttpServletResponse.class));

		assertFalse(modified.getJSONObject(id).getBoolean("a"));
	}

	@Test
	public void doesNotAddExtraPlugins() throws Exception {
		String id = "plugin";

		PluginDescriptor p = new PluginDescriptor();
		p.setDefaultConfiguration(JSONObject.fromObject("{'a' : true }"));
		p.setId(id);

		PluginDefaultsConfigFilter filter = new PluginDefaultsConfigFilter(
				mockConfig(p));

		JSONObject config = JSONObject.fromObject("{}");

		JSONObject modified = filter
				.modifyConfig(config, mock(HttpServletRequest.class),
						mock(HttpServletResponse.class));

		assertFalse(modified.has(id));
	}

	@Test
	public void ignoresInvalidPlugins() throws Exception {
		PluginDefaultsConfigFilter filter = new PluginDefaultsConfigFilter(
				mockConfig());

		JSONObject config = JSONObject
				.fromObject("{'plugin' : { 'a' : true }}");

		JSONObject modified = filter
				.modifyConfig(config, mock(HttpServletRequest.class),
						mock(HttpServletResponse.class));

		assertTrue(modified.getJSONObject("plugin").getBoolean("a"));
	}

	private Config mockConfig(PluginDescriptor... descriptors)
			throws IOException {
		Config config = mock(Config.class);
		when(config.getPluginDescriptors(any(JSONObject.class))).thenReturn(
				descriptors);

		return config;
	}
}
