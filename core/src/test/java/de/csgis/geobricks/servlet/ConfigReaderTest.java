package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.csgis.geobricks.Geobricks;

public class ConfigReaderTest {
	private static File confDir, appProperties;

	private ServletContext context;
	private Map<Object, Object> attributes;

	@BeforeClass
	public static void createProperties() throws Exception {
		confDir = File.createTempFile("geobricks-test", "");
		confDir.delete();
		confDir.mkdir();
		appProperties = new File(confDir, "app.properties");
		new Properties().store(new FileOutputStream(appProperties), "");
	}

	@AfterClass
	public static void deleteProperties() throws Exception {
		appProperties.delete();
		confDir.delete();
	}

	@Before
	public void setup() throws Exception {
		FileUtils.deleteDirectory(confDir);
		confDir.mkdir();

		attributes = new HashMap<Object, Object>();
		context = mock(ServletContext.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				attributes.put(args[0], args[1]);
				return null;
			}
		}).when(context).setAttribute(anyString(), anyObject());
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return attributes.get(invocation.getArguments()[0]);
			}
		}).when(context).getAttribute(anyString());
		context.setAttribute(Geobricks.ATTR_CONF_DIR, confDir.getAbsolutePath());
	}

	@Test
	public void loadPropertiesOnConstructor() throws Exception {
		Properties properties = new Properties();
		properties.setProperty("k1", "v1");
		properties.store(new FileOutputStream(appProperties), null);

		ConfigReader config = new ConfigReader(context);
		Properties app = config.getAppProperties();
		assertEquals(1, app.size());
		assertEquals("v1", app.getProperty("k1"));
	}

	@Test
	public void reloadPropertiesIfModifiedRecently() throws Exception {
		Properties properties = new Properties();
		properties.setProperty("k1", "v1");
		properties.store(new FileOutputStream(appProperties), null);

		ConfigReader config = new ConfigReader(context);

		// Last modified only takes seconds into account, not millis. We wait
		// for at least one second.
		Thread.sleep(1000);

		properties.setProperty("k1", "v2");
		properties.setProperty("k2", "v3");
		properties.store(new FileOutputStream(appProperties), null);
		appProperties.setLastModified(System.currentTimeMillis());

		Properties app = config.getAppProperties();
		assertEquals(2, app.size());
		assertEquals("v2", app.getProperty("k1"));
		assertEquals("v3", app.getProperty("k2"));
	}

	@Test
	public void returnsEmptyMapIfNoPluginConfigs() throws Exception {
		ConfigReader config = new ConfigReader(context);
		assertEquals(0, config.getPluginConfigs().size());
	}

	@Test
	public void addPluginConfigIfFileCreated() throws Exception {
		ConfigReader config = new ConfigReader(context);
		assertEquals(0, config.getPluginConfigs().size());

		File pluginConf = new File(confDir, "plugin.json");
		JSONObject json = JSONObject.fromObject("{module1 : [1,2,3]}");
		IOUtils.write(json.toString(), new FileOutputStream(pluginConf));

		assertEquals(1, config.getPluginConfigs().size());
		assertEquals(json, config.getPluginConfigs().get("plugin"));

		pluginConf.delete();
	}

	@Test
	public void reloadPluginConfigIfModifiedRecently() throws Exception {
		ConfigReader config = new ConfigReader(context);
		File pluginConf = new File(confDir, "plugin.json");

		JSONObject json = JSONObject.fromObject("{module1 : [1,2,3]}");
		IOUtils.write(json.toString(), new FileOutputStream(pluginConf));
		assertEquals(json, config.getPluginConfigs().get("plugin"));

		// Last modified only takes seconds into account, not millis. We wait
		// for at least one second.
		Thread.sleep(1000);

		json = JSONObject.fromObject("{module1 : [4,5,6]}");
		IOUtils.write(json.toString(), new FileOutputStream(pluginConf));
		assertEquals(json, config.getPluginConfigs().get("plugin"));

		pluginConf.delete();
	}

	@Test
	public void removePluginConfigIfFileDeleted() throws Exception {
		ConfigReader config = new ConfigReader(context);
		File pluginConf = new File(confDir, "plugin.json");

		JSONObject json = JSONObject.fromObject("{module1 : [1,2,3]}");
		IOUtils.write(json.toString(), new FileOutputStream(pluginConf));
		assertEquals(json, config.getPluginConfigs().get("plugin"));

		pluginConf.delete();
		assertEquals(0, config.getPluginConfigs().size());
	}
}