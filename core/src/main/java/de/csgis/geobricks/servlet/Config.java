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

/**
 * Class for managing all the application configuration (application descriptor,
 * <code>app.properties</code> file, plugin descriptors, etc.
 * 
 * @author vicgonco
 */
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

	private List<ConfigFilter> filters;

	public Config(ServletContext context, PluginDescriptorReader reader)
			throws IOException {
		this.context = context;
		this.reader = reader;
		this.lastAppPropertiesAccess = -1;

		InputStream stream = context.getResourceAsStream(APP_CONF_PATH);
		this.gbappConf = JSONObject.fromObject(IOUtils.toString(stream));
		stream.close();

		this.filters = new ArrayList<ConfigFilter>();
		this.filters.add(new PluginDependenciesConfigFilter(this.reader));
		this.filters.add(new ConfigDirOverridesConfigFilter(getConfigDir()));
		this.filters.add(new RoleSpecificConfigFilter(getConfigDir()));
		this.filters.add(new PluginDefaultsConfigFilter(this));

		updateAppProperties();
	}

	/**
	 * Adds a new {@link ConfigFilter} to apply to the application
	 * configuration.
	 * 
	 * @param filter
	 *            the {@link ConfigFilter} to apply.
	 */
	public void addConfigFilter(ConfigFilter filter) {
		this.filters.add(filter);
	}

	/**
	 * Obtains the application configuration for the specified request.
	 * 
	 * @param request
	 *            The HTTP request for the application configuration.
	 * @param response
	 *            The HTTP response, in case it needs to be modified (cookies,
	 *            headers, etc.)
	 * @return The application configuration for the specified request.
	 */
	public JSONObject getApplicationConf(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject conf = this.gbappConf;
		for (ConfigFilter handler : this.filters) {
			try {
				conf = handler.modifyConfig(conf, request, response);
			} catch (IOException e) {
				logger.error("An error has occurred while "
						+ "modifying the configuration. Ignoring handler", e);
			}
		}
		return conf;
	}

	/**
	 * Obtains the configuration directory for the application.
	 * 
	 * @return The path of the configuration directory.
	 */
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

	/**
	 * Obtains the <code>app.properties</code> file contents.
	 * 
	 * @return
	 */
	public Properties getAppProperties() {
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
		return this.appProperties;
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

	/**
	 * Obtains the plugin descriptors for the given request.
	 * 
	 * @param request
	 *            The HTTP request for the application configuration.
	 * @param response
	 *            The HTTP response, in case it needs to be modified (cookies,
	 *            headers, etc.)
	 * @return The plugin descriptors.
	 * @throws IOException
	 *             if any I/O error occurs while reading the plugin descriptors.
	 */
	public PluginDescriptor[] getPluginDescriptors(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		return getPluginDescriptors(getApplicationConf(request, response),
				this.reader);
	}

	/**
	 * Obtains the plugin descriptors for the given application configuration.
	 * 
	 * @param gbappConf
	 *            The application configuration with the plugins to obtain the
	 * @return The plugin descriptors.
	 * @throws IOException
	 *             if any I/O error occurs while reading the plugin descriptors.
	 */
	public PluginDescriptor[] getPluginDescriptors(JSONObject gbappConf)
			throws IOException {
		return getPluginDescriptors(gbappConf, this.reader);
	}

	/**
	 * Obtains the plugin descriptors for the given application configuration.
	 * 
	 * @param gbappConf
	 *            The application configuration with the plugins to obtain the
	 * @return The plugin descriptors.
	 * @param reader
	 *            The {@link PluginDescriptorReader} used for reading the
	 *            descriptors.
	 * @throws IOException
	 *             if any I/O error occurs while reading the plugin descriptors.
	 */
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
