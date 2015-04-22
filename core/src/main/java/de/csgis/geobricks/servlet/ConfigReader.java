package de.csgis.geobricks.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.csgis.geobricks.Geobricks;

public class ConfigReader {
	private static final Logger logger = Logger.getLogger(ConfigReader.class);

	private static final String ATTR_PLUGIN_CONFIGS = ConfigReader.class
			.getCanonicalName() + "_plugin_confs";
	private static final String ATTR_LAST_ACCESSES = ConfigReader.class
			.getCanonicalName() + "_last_accesses";

	private ServletContext context;

	public ConfigReader(ServletContext context) throws IOException {
		this.context = context;
		updateAppProperties();
	}

	public String getConfigDir() {
		return context.getAttribute(Geobricks.ATTR_CONF_DIR).toString();
	}

	public Properties getAppProperties() {
		Long lastAccess = getLastAccessesFromContext().get("app.properties");
		Object appPropertiesObj = context
				.getAttribute(Geobricks.ATTR_APP_PROPERTIES);

		boolean needsUpdate = lastAccess == null || appPropertiesObj == null;
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

		return (Properties) context.getAttribute(Geobricks.ATTR_APP_PROPERTIES);
	}

	public Map<String, JSONObject> getPluginConfigs() {
		Map<String, Long> lastAccesses = getLastAccessesFromContext();
		Map<String, JSONObject> pluginConfigs = getPluginConfigsFromContext();

		File config = new File(getConfigDir());
		File[] files = config.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
		});

		if (files == null) {
			pluginConfigs.clear();
			return pluginConfigs;
		}

		for (String name : pluginConfigs.keySet()) {
			File file = new File(getConfigDir(), name + ".json");
			if (!file.exists()) {
				pluginConfigs.remove(name);
			}
		}

		for (File file : files) {
			String name = file.getName();
			JSONObject pluginConfig = pluginConfigs.get(name);
			Long lastAccess = lastAccesses.get(name);

			boolean needsUpdate = pluginConfig == null || lastAccess == null;
			if (lastAccess != null) {
				needsUpdate |= file.lastModified() > lastAccess;
			}

			if (needsUpdate) {
				try {
					String content = IOUtils
							.toString(new FileInputStream(file));
					JSONObject json = JSONObject.fromObject(content);

					String basename = name.substring(0,
							name.lastIndexOf(".json"));
					pluginConfigs.put(basename, json);
					lastAccesses
							.put(basename, new Long(System.currentTimeMillis()));
				} catch (IOException e) {
					logger.error("Cannot read JSON plugin "
							+ "config from config dir", e);
					pluginConfigs.remove(name);
				}
			}
		}

		return pluginConfigs;
	}

	private void updateAppProperties() throws IOException {
		Map<String, Long> lastAccesses = getLastAccessesFromContext();
		File file = new File(context.getAttribute(Geobricks.ATTR_CONF_DIR)
				.toString(), "app.properties");
		Properties appProperties = new Properties();
		if (file.exists()) {
			FileInputStream in = new FileInputStream(file);
			appProperties.load(in);
			in.close();
		}

		lastAccesses
				.put("app.properties", new Long(System.currentTimeMillis()));
		context.setAttribute(Geobricks.ATTR_APP_PROPERTIES, appProperties);
	}

	private Map<String, Long> getLastAccessesFromContext() {
		@SuppressWarnings("unchecked")
		Map<String, Long> lastAccesses = (Map<String, Long>) context
				.getAttribute(ATTR_LAST_ACCESSES);
		if (lastAccesses == null) {
			lastAccesses = new HashMap<String, Long>();
			context.setAttribute(ATTR_LAST_ACCESSES, lastAccesses);
		}

		return lastAccesses;
	}

	private Map<String, JSONObject> getPluginConfigsFromContext() {
		@SuppressWarnings("unchecked")
		Map<String, JSONObject> pluginConfigs = (Map<String, JSONObject>) context
				.getAttribute(ATTR_PLUGIN_CONFIGS);
		if (pluginConfigs == null) {
			pluginConfigs = new HashMap<String, JSONObject>();
			context.setAttribute(ATTR_PLUGIN_CONFIGS, pluginConfigs);
		}

		return pluginConfigs;
	}
}
