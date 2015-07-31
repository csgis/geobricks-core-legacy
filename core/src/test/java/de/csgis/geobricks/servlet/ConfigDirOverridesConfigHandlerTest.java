package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigDirOverridesConfigHandlerTest {
	private File dir;
	private ConfigDirOverridesConfigHandler handler;

	@Before
	public void setup() throws IOException {
		dir = File.createTempFile("geobricks", "");
		dir.delete();
		dir.mkdir();
		handler = new ConfigDirOverridesConfigHandler(dir.getAbsolutePath());
	}

	@After
	public void teardown() throws IOException {
		FileUtils.deleteDirectory(dir);
	}

	@Test
	public void addsPlugin() throws Exception {
		JSONObject config = JSONObject.fromObject("{}");

		File tmp = new File(dir, "olmap.json");
		FileWriter writer = new FileWriter(tmp);
		IOUtils.write("{ 'map' : { 'div' : 'mymap' } }", writer);
		writer.close();

		JSONObject modified = handler.modifyConfig(config, null, null);
		assertTrue(modified.has("olmap"));
		JSONObject olmap = modified.getJSONObject("olmap");
		assertEquals("mymap", olmap.getJSONObject("map").getString("div"));

		tmp.delete();
	}

	@Test
	public void modifiesPluginConfig() throws Exception {
		JSONObject config = JSONObject
				.fromObject("{ 'olmap' : { 'map' : { 'div' : 'mymap' }}}");

		File tmp = new File(dir, "olmap.json");
		FileWriter writer = new FileWriter(tmp);
		IOUtils.write("{ 'map' : { 'div' : 'mymap' }, "
				+ "'zoom' : { 'tooltip' : 'Zoom' } }", writer);
		writer.close();

		JSONObject modified = handler.modifyConfig(config, null, null);
		assertTrue(modified.has("olmap"));
		JSONObject map = modified.getJSONObject("olmap");
		assertEquals("mymap", map.getJSONObject("map").getString("div"));
		assertEquals("Zoom", map.getJSONObject("zoom").getString("tooltip"));

		tmp.delete();
	}

	@Test
	public void modifiesModuleConfigIfExists() throws Exception {
		JSONObject config = JSONObject
				.fromObject("{ 'olmap' : { 'map' : { 'div' : 'mymap' }}}");

		File tmp = new File(dir, "map.json");
		FileWriter writer = new FileWriter(tmp);
		IOUtils.write("{ 'div' : 'anotherdiv' }", writer);
		writer.close();

		JSONObject modified = handler.modifyConfig(config, null, null);
		assertTrue(modified.has("olmap"));
		assertEquals("anotherdiv", modified.getJSONObject("olmap")
				.getJSONObject("map").getString("div"));

		tmp.delete();
	}
}
