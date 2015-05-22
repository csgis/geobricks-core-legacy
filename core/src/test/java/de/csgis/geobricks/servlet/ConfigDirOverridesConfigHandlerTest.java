package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertFalse;
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

		File tmp = new File(dir, "plugin.json");
		FileWriter writer = new FileWriter(tmp);
		IOUtils.write("{ 'a' : true }", writer);
		writer.close();

		JSONObject modified = handler.modifyConfig(config, null, null);
		System.out.println(modified);
		assertTrue(modified.has("plugin"));
		assertTrue(modified.getJSONObject("plugin").getBoolean("a"));

		tmp.delete();
	}

	@Test
	public void modifiesPluginConfig() throws Exception {
		JSONObject config = JSONObject.fromObject("{ 'a' : false }");

		File tmp = new File(dir, "plugin.json");
		FileWriter writer = new FileWriter(tmp);
		IOUtils.write("{ 'a' : true, 'b' : false }", writer);
		writer.close();

		JSONObject modified = handler.modifyConfig(config, null, null);
		System.out.println(modified);
		assertTrue(modified.has("plugin"));
		assertTrue(modified.getJSONObject("plugin").getBoolean("a"));
		assertFalse(modified.getJSONObject("plugin").getBoolean("b"));

		tmp.delete();
	}
}
