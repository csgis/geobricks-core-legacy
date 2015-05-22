package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.junit.Test;

import de.csgis.geobricks.PluginDescriptor;

public class PluginDefaultsConfigHandlerTest {
	@Test
	public void addsDefaultConfig() throws Exception {
		String id = "plugin";

		PluginDescriptor p = new PluginDescriptor();
		p.setDefaultConfiguration(JSONObject.fromObject("{'a' : true }"));
		p.setId(id);

		PluginDefaultsConfigHandler handler = new PluginDefaultsConfigHandler(
				new PluginDescriptor[] { p });

		JSONObject config = JSONObject.fromObject("{'" + id + "' : {}}");

		JSONObject modified = handler
				.modifyConfig(config, mock(HttpServletRequest.class),
						mock(HttpServletResponse.class));

		assertTrue(modified.getJSONObject(id).getBoolean("a"));
	}

	@Test
	public void doesNotOverrideConfig() throws Exception {
		String id = "plugin";

		PluginDescriptor p = new PluginDescriptor();
		p.setDefaultConfiguration(JSONObject.fromObject("{'a' : true }"));
		p.setId(id);

		PluginDefaultsConfigHandler handler = new PluginDefaultsConfigHandler(
				new PluginDescriptor[] { p });

		JSONObject config = JSONObject.fromObject("{'" + id
				+ "' : { 'a' : false }}");

		JSONObject modified = handler
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

		PluginDefaultsConfigHandler handler = new PluginDefaultsConfigHandler(
				new PluginDescriptor[] { p });

		JSONObject config = JSONObject.fromObject("{}");

		JSONObject modified = handler
				.modifyConfig(config, mock(HttpServletRequest.class),
						mock(HttpServletResponse.class));

		assertFalse(modified.has(id));
	}

	@Test
	public void ignoresInvalidPlugins() throws Exception {
		PluginDefaultsConfigHandler handler = new PluginDefaultsConfigHandler(
				new PluginDescriptor[0]);

		JSONObject config = JSONObject
				.fromObject("{'plugin' : { 'a' : true }}");

		JSONObject modified = handler
				.modifyConfig(config, mock(HttpServletRequest.class),
						mock(HttpServletResponse.class));

		assertTrue(modified.getJSONObject("plugin").getBoolean("a"));
	}
}
