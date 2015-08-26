package de.csgis.geobricks.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginDescriptorReader;

public class Config {
	private static final Logger logger = Logger.getLogger(Config.class);

	public static final String APP_CONF_PATH = "/WEB-INF/conf/gbapp-conf.json";
	private static final String DEFAULT_CONF_PATH = "WEB-INF" + File.separator
			+ "default_conf";

	private ServletContext context;
	private String confDir;
	private JSONObject gbappConf;
	private Properties appProperties;
	private long lastAppPropertiesAccess;
	private PluginDescriptorReader reader;

	private List<ConfigHandler> handlers;

	public void init(ServletContext context, PluginDescriptorReader reader)
			throws IOException {
		this.context = context;
		this.reader = reader;
		this.lastAppPropertiesAccess = -1;

		InputStream stream = context.getResourceAsStream(APP_CONF_PATH);
		this.gbappConf = JSONObject.fromObject(IOUtils.toString(stream));
		stream.close();

		this.handlers = new ArrayList<ConfigHandler>();
		this.handlers.add(new PluginDependenciesConfigHandler(this.reader));
		this.handlers.add(new ConfigDirOverridesConfigHandler(getConfigDir()));
		this.handlers.add(new RoleSpecificConfigHandler(getConfigDir()));
		this.handlers.add(new PluginDefaultsConfigHandler(this));

		updateAppProperties();
	}

	public void addConfigHandler(ConfigHandler handler) {
		this.handlers.add(handler);
	}

	public JSONObject getApplicationConf(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject conf = this.gbappConf;
		for (ConfigHandler handler : this.handlers) {
			try {
				conf = handler.modifyConfig(conf, request, response);
			} catch (IOException e) {
				logger.error("An error has occurred while "
						+ "modifying the configuration. Ignoring handler", e);
			}
		}
		return conf;
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
		File file = new File(getConfigDir(), "app.properties");
		if (appProperties == null
				|| file.lastModified() > this.lastAppPropertiesAccess) {
			try {
				updateAppProperties();
			} catch (IOException e) {
				logger.error("Cannot read app.properties file."
						+ "Using previous values if any");
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

		this.lastAppPropertiesAccess = System.currentTimeMillis();
	}

	public PluginDescriptor[] getPluginDescriptors(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		return getPluginDescriptors(getApplicationConf(request, response),
				this.reader);
	}

	public PluginDescriptor[] getPluginDescriptors(JSONObject gbappConf)
			throws IOException {
		return getPluginDescriptors(gbappConf, this.reader);
	}

	PluginDescriptor[] getPluginDescriptors(JSONObject gbappConf,
			PluginDescriptorReader reader) throws IOException {
		List<String> plugins = new ArrayList<String>();
		Iterator<?> iterator = gbappConf.keys();
		while (iterator.hasNext()) {
			plugins.add(iterator.next().toString());
		}
		return reader.getDescriptors(plugins);
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
