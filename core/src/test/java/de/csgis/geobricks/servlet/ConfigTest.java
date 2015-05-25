package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

@SuppressWarnings("unchecked")
public class ConfigTest {
	private static final String DEFAULT_CONF_PATH = ConfigTest.class
			.getResource(".").getPath()
			+ File.separator
			+ "WEB-INF"
			+ File.separator + "default_conf";
	private static final String PLUGIN_ID = "myplugin";

	private static File confDir, appProperties, roleDir;

	private ServletContext context;
	private PluginDescriptor descriptor;
	private String gbappConf;
	private PluginDescriptorReader reader;

	private Map<Object, Object> attributes;

	@BeforeClass
	public static void createConfigDir() throws Exception {
		confDir = File.createTempFile("geobricks-test", "");
		confDir.delete();

		appProperties = new File(confDir, "app.properties");

		roleDir = new File(confDir, RoleSpecificConfigHandler.ROLE_DIR);
	}

	@Before
	public void setup() throws Exception {
		context = mock(ServletContext.class);
		when(context.getContextPath()).thenReturn(confDir.getName());

		// Conf dir
		FileUtils.deleteDirectory(confDir);
		confDir.mkdir();
		System.setProperty(Geobricks.PROP_GEOBRICKS_CONF, confDir
				.getParentFile().getAbsolutePath());
		when(context.getRealPath("/")).thenReturn(
				getClass().getResource(".").getPath());

		// Context attributes
		attributes = new HashMap<Object, Object>();
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

		// gbappConf
		gbappConf = "{}";
		when(context.getResourceAsStream(anyString())).thenAnswer(
				new Answer<InputStream>() {
					@Override
					public InputStream answer(InvocationOnMock invocation)
							throws Throwable {
						return new ByteArrayInputStream(gbappConf.getBytes());
					}
				});

		descriptor = new PluginDescriptor();
		descriptor.setId(PLUGIN_ID);

		reader = mock(PluginDescriptorReader.class);
		when(reader.getDescriptors(anyList())).thenReturn(
				new PluginDescriptor[] { descriptor });

		FileUtils.deleteDirectory(roleDir);
		roleDir.mkdir();
	}

	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(confDir);
	}

	@Test
	public void undefinedConfDir() throws Exception {
		System.clearProperty(Geobricks.PROP_GEOBRICKS_CONF);
		when(context.getContextPath()).thenReturn(confDir.getName());

		Config config = new Config();
		config.init(context);
		assertEquals(DEFAULT_CONF_PATH, config.getConfigDir());
	}

	@Test
	public void nonExistingConfDir() throws Exception {
		System.setProperty(Geobricks.PROP_GEOBRICKS_CONF, "non_existing_dir");
		when(context.getContextPath()).thenReturn("viewer");

		Config config = new Config();
		config.init(context);
		assertEquals(DEFAULT_CONF_PATH, config.getConfigDir());
	}

	@Test
	public void nonExistingConfDirForApp() throws Exception {
		System.setProperty(Geobricks.PROP_GEOBRICKS_CONF, confDir.getParent());
		when(context.getContextPath()).thenReturn("viewer");

		Config config = new Config();
		config.init(context);
		assertEquals(DEFAULT_CONF_PATH, config.getConfigDir());
	}

	@Test
	public void confDirFromVariable() throws Exception {
		System.setProperty(Geobricks.PROP_GEOBRICKS_CONF, confDir.getParent());
		when(context.getContextPath()).thenReturn(confDir.getName());

		Config config = new Config();
		config.init(context);

		assertEquals(confDir.getAbsolutePath(), config.getConfigDir());
	}

	@Test
	public void loadPropertiesOnInit() throws Exception {
		Properties properties = new Properties();
		properties.setProperty("k1", "v1");
		properties.store(new FileOutputStream(appProperties), null);

		Config config = new Config();
		config.init(context, reader);

		Properties app = config.getAppProperties();
		assertEquals(1, app.size());
		assertEquals("v1", app.getProperty("k1"));
	}

	@Test
	public void reloadPropertiesIfModifiedRecently() throws Exception {
		Properties properties = new Properties();
		properties.setProperty("k1", "v1");
		properties.store(new FileOutputStream(appProperties), null);

		Config config = new Config();
		config.init(context, reader);

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
	public void addPluginConfigIfFileCreated() throws Exception {
		gbappConf = "{" + PLUGIN_ID + ":{}}";

		Config config = new Config();
		config.init(context, reader);

		assertEquals(
				new JSONObject(),
				config.getApplicationConf(mock(HttpServletRequest.class),
						mock(HttpServletResponse.class)).get(PLUGIN_ID));

		File pluginConf = new File(confDir, PLUGIN_ID + ".json");
		JSONObject json = JSONObject.fromObject("{module1 : [1,2,3]}");
		IOUtils.write(json.toString(), new FileOutputStream(pluginConf));

		assertEquals(
				1,
				config.getApplicationConf(mock(HttpServletRequest.class),
						mock(HttpServletResponse.class)).size());
		assertEquals(
				json,
				config.getApplicationConf(mock(HttpServletRequest.class),
						mock(HttpServletResponse.class)).get(PLUGIN_ID));

		pluginConf.delete();
	}

	@Test
	public void reloadPluginConfigIfModifiedRecently() throws Exception {
		gbappConf = "{" + PLUGIN_ID + ":{}}";

		Config config = new Config();
		config.init(context, reader);

		File pluginConf = new File(confDir, PLUGIN_ID + ".json");

		JSONObject json = JSONObject.fromObject("{module1 : [1,2,3]}");
		IOUtils.write(json.toString(), new FileOutputStream(pluginConf));
		assertEquals(
				json,
				config.getApplicationConf(mock(HttpServletRequest.class),
						mock(HttpServletResponse.class)).get(PLUGIN_ID));

		// Last modified only takes seconds into account, not millis. We wait
		// for at least one second.
		Thread.sleep(1000);

		json = JSONObject.fromObject("{module1 : [4,5,6]}");
		IOUtils.write(json.toString(), new FileOutputStream(pluginConf));
		assertEquals(
				json,
				config.getApplicationConf(mock(HttpServletRequest.class),
						mock(HttpServletResponse.class)).get(PLUGIN_ID));

		pluginConf.delete();
	}

	@Test
	public void removePluginConfigIfFileDeleted() throws Exception {
		gbappConf = "{" + PLUGIN_ID + ":{}}";

		Config config = new Config();
		config.init(context, reader);

		File pluginConf = new File(confDir, PLUGIN_ID + ".json");

		JSONObject json = JSONObject.fromObject("{module1 : [1,2,3]}");
		IOUtils.write(json.toString(), new FileOutputStream(pluginConf));
		assertEquals(
				json,
				config.getApplicationConf(mock(HttpServletRequest.class),
						mock(HttpServletResponse.class)).get(PLUGIN_ID));

		pluginConf.delete();
		assertEquals(
				new JSONObject(),
				config.getApplicationConf(mock(HttpServletRequest.class),
						mock(HttpServletResponse.class)).get(PLUGIN_ID));
	}

	@Test
	public void customPluginConfig() throws Exception {
		gbappConf = "{" + PLUGIN_ID + ": { mymodule : { enabled : true }}}";

		Config config = new Config();
		config.init(context, reader);

		assertTrue(config
				.getApplicationConf(mock(HttpServletRequest.class),
						mock(HttpServletResponse.class))
				.getJSONObject(PLUGIN_ID).getJSONObject("mymodule")
				.getBoolean("enabled"));
	}

	@Test
	public void defaultPluginConfigWhenEmpty() throws Exception {
		descriptor.setDefaultConfiguration(JSONObject
				.fromObject("{ mymodule : { enabled : false }}"));
		gbappConf = "{" + PLUGIN_ID + ": {}}";

		Config config = new Config();
		config.init(context, reader);

		assertFalse(config
				.getApplicationConf(mock(HttpServletRequest.class),
						mock(HttpServletResponse.class))
				.getJSONObject(PLUGIN_ID).getJSONObject("mymodule")
				.getBoolean("enabled"));
	}

	@Test
	public void getConfiguredAppId() throws Exception {
		String configuredId = "configuredid";
		when(context.getInitParameter("geobricks-app-id")).thenReturn(
				configuredId);
		when(context.getContextPath()).thenReturn("/myapp");

		Config config = new Config();
		assertEquals(config.getApplicationId(context), configuredId);
	}

	@Test
	public void getAppIdFromURL() throws Exception {
		String urlId = "myapp";
		when(context.getInitParameter("geobricks-app-id")).thenReturn(null);
		when(context.getContextPath()).thenReturn("/" + urlId);

		Config config = new Config();
		assertEquals(urlId, config.getApplicationId(context));
	}

	@Test
	public void getSpecificPluginConfForRole() throws Exception {
		gbappConf = "{" + PLUGIN_ID + ":{}}";
		String role = "r1";

		File pluginConfFile = new File(roleDir, role + ".json");
		JSONObject pluginSpecificConf = JSONObject
				.fromObject("{module1 : [1,2,3]}");
		JSONObject json = new JSONObject();
		json.element(PLUGIN_ID, pluginSpecificConf);
		IOUtils.write(json.toString(), new FileOutputStream(pluginConfFile));

		Config config = new Config();
		config.init(context, reader);

		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getAttribute(Geobricks.ATTR_ROLE)).thenReturn(role);
		assertEquals(
				pluginSpecificConf,
				config.getApplicationConf(request,
						mock(HttpServletResponse.class)).get(PLUGIN_ID));
	}

	@Test
	public void getExtraPluginsForRole() throws Exception {
		gbappConf = "{" + PLUGIN_ID + ":{}}";

		String role = "r1";
		String anotherPlugin = "another_" + PLUGIN_ID;

		File pluginConfFile = new File(roleDir, role + ".json");
		JSONObject pluginSpecificConf = JSONObject
				.fromObject("{module1 : [1,2,3]}");
		JSONObject json = new JSONObject();
		json.element(anotherPlugin, pluginSpecificConf);
		IOUtils.write(json.toString(), new FileOutputStream(pluginConfFile));

		Config config = new Config();
		config.init(context, reader);

		HttpServletRequest request = mock(HttpServletRequest.class);
		// Plugin is available for role
		when(request.getAttribute(Geobricks.ATTR_ROLE)).thenReturn(role);
		assertEquals(
				pluginSpecificConf,
				config.getApplicationConf(request,
						mock(HttpServletResponse.class)).get(anotherPlugin));
		// Plugin is not available by default
		when(request.getAttribute(Geobricks.ATTR_ROLE)).thenReturn(null);
		assertNull(config.getApplicationConf(request,
				mock(HttpServletResponse.class)).get(anotherPlugin));
	}
}
