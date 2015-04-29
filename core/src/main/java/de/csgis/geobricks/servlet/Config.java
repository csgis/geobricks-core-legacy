package de.csgis.geobricks.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.JSONUtils;
import de.csgis.geobricks.PluginDescriptor;

public class Config {
	private static final Logger logger = Logger.getLogger(Config.class);

	public static final String APP_CONF_PATH = "/WEB-INF/conf/gbapp-conf.json";
	private static final String DEFAULT_CONF_PATH = "WEB-INF" + File.separator
			+ "default_conf";

	private ServletContext context;
	private String confDir;
	private PluginDescriptor[] descriptors;
	private JSONObject originalAppConf, overridenAppConf;
	private Properties appProperties;
	private Map<String, Long> lastFileAccesses = new HashMap<String, Long>();
	private Map<String, JSONObject> pluginConfOverrides = new HashMap<String, JSONObject>();

	public void init(ServletContext context) throws IOException {
		init(context, new PluginDescriptorReader());
	}

	void init(ServletContext context, PluginDescriptorReader reader)
			throws IOException {
		this.context = context;
		this.lastFileAccesses = new HashMap<String, Long>();
		this.pluginConfOverrides = new HashMap<String, JSONObject>();

		// Read gbapp-conf.json
		InputStream stream = context.getResourceAsStream(APP_CONF_PATH);
		String content = IOUtils.toString(stream);
		this.originalAppConf = JSONObject.fromObject(content);
		stream.close();

		// Get plugin descriptors
		List<String> plugins = new ArrayList<String>();
		Iterator<?> iterator = this.originalAppConf.keys();
		while (iterator.hasNext()) {
			plugins.add(iterator.next().toString());
		}
		this.descriptors = reader.getDescriptors(plugins);

		// Apply default plugin configurations to gbapp-conf
		for (Object key : this.originalAppConf.keySet()) {
			String pluginId = key.toString();
			JSONObject pluginConf = this.originalAppConf
					.getJSONObject(pluginId);
			JSONObject defaultConf = getDefaultConfiguration(pluginId);
			if (defaultConf != null) {
				JSONObject merged = JSONUtils.merge(defaultConf, pluginConf);
				this.originalAppConf.put(pluginId, merged);
			}
		}
		this.overridenAppConf = JSONObject.fromObject(this.originalAppConf);

		applyExternalPluginConfs();

		updateAppProperties();
	}

	public JSONObject getApplicationConf() {
		applyExternalPluginConfs();
		return this.overridenAppConf;
	}

	private void applyExternalPluginConfs() {
		boolean updated = updatePluginConfsIfNeeded();
		if (updated) {
			this.overridenAppConf = JSONObject.fromObject(this.originalAppConf);
			for (Object plugin : this.originalAppConf.keySet()) {
				JSONObject override = this.pluginConfOverrides.get(plugin);
				if (override != null) {
					this.overridenAppConf.put(plugin, override);
				}
			}
		}
	}

	/**
	 * Get the default configuration for the specified plugin.
	 * 
	 * @param id
	 *            The identifier of the required plugin.
	 * @return The default configuration of the plugin or <code>null</code> if
	 *         the plugin cannot be found.
	 */
	private JSONObject getDefaultConfiguration(String id) {
		for (PluginDescriptor descriptor : descriptors) {
			if (descriptor.getId().equals(id)) {
				return descriptor.getDefaultConfiguration();
			}
		}
		return null;
	}

	private boolean updatePluginConfsIfNeeded() {
		File config = new File(getConfigDir());
		File[] files = config.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
		});

		if (files == null) {
			this.pluginConfOverrides.clear();
			return true;
		}

		boolean updated = false;
		for (String name : this.pluginConfOverrides.keySet()) {
			File file = new File(getConfigDir(), name + ".json");
			if (!file.exists()) {
				this.pluginConfOverrides.remove(name);
				updated = true;
			}
		}

		for (File file : files) {
			String name = file.getName();
			JSONObject pluginConfig = this.pluginConfOverrides.get(name);
			Long lastAccess = this.lastFileAccesses.get(name);

			boolean needsUpdate = pluginConfig == null || lastAccess == null;
			if (lastAccess != null) {
				needsUpdate |= file.lastModified() > lastAccess;
			}

			if (needsUpdate) {
				updated = true;
				try {
					String content = IOUtils
							.toString(new FileInputStream(file));
					JSONObject json = JSONObject.fromObject(content);

					String basename = name.substring(0,
							name.lastIndexOf(".json"));
					this.pluginConfOverrides.put(basename, json);
					this.lastFileAccesses.put(basename,
							new Long(System.currentTimeMillis()));
				} catch (IOException e) {
					logger.error("Cannot read JSON plugin "
							+ "config from config dir", e);
					this.pluginConfOverrides.remove(name);
				}
			}
		}

		return updated;
	}

	public String getConfigDir() {
		if (confDir == null) {
			String conf = System.getProperty(Geobricks.PROP_GEOBRICKS_CONF);

			// If the system property is not specified, the configuration
			// is directly in the WEB-INF/default_conf directory
			this.confDir = context.getRealPath("/") + File.separator
					+ DEFAULT_CONF_PATH;
			if (conf != null && new File(conf).exists()) {
				// If the system property is specified and the app subdirectory
				// exists, the configuration is in GEOBRICKS_CONF_DIR/<app>/
				String appId = getApplicationId(context);
				File appDir = new File(conf, appId);
				if (appDir.exists()) {
					this.confDir = conf + File.separator + appId;
				}
			}
		}
		return confDir;
	}

	public Properties getAppProperties() {
		updateAppPropertiesIfNeeded();
		return this.appProperties;
	}

	private void updateAppPropertiesIfNeeded() {
		Long lastAccess = this.lastFileAccesses.get("app.properties");

		boolean needsUpdate = lastAccess == null || appProperties == null;
		if (lastAccess != null) {
			File file = new File(getConfigDir(), "app.properties");
			needsUpdate |= file.lastModified() > lastAccess;
		}

		if (needsUpdate) {
			try {
				updateAppProperties();
			} catch (IOException e) {
				logger.error("Cannot read app.properties file. Using previous values if any");
			}
		}
	}

	private void updateAppProperties() throws IOException {
		File file = new File(getConfigDir(), "app.properties");
		this.appProperties = new Properties();
		if (file.exists()) {
			FileInputStream in = new FileInputStream(file);
			this.appProperties.load(in);
			in.close();
		}

		this.lastFileAccesses.put("app.properties",
				new Long(System.currentTimeMillis()));
	}

	public PluginDescriptor[] getPluginDescriptors() {
		return this.descriptors;
	}

	/**
	 * Returns the application id. It can be defined using the
	 * {@link Geobricks#INIT_PARAM_ID} init parameter. By default it is the
	 * context path ({@link ServletContext#getContextPath()}).
	 * 
	 * @param context
	 *            the application context.
	 * @return the application id.
	 */
	String getApplicationId(ServletContext context) {
		String id = context.getInitParameter(Geobricks.INIT_PARAM_ID);
		if (id == null) {
			id = context.getContextPath();
			if (id.startsWith("/")) {
				id = id.substring(1);
			}
		}
		return id;
	}
}
