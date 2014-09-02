package de.csgis.geobricks.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

/**
 * Reads the application configuration from
 * {@link ApplicationListener#APP_CONF_PATH} and updates the following servlet
 * attributes:
 * 
 * <ul>
 * <li>Sets {@link Geobricks#ATTR_PLUGINS_CONF} with the app-specific plugin
 * configuration.</li>
 * <li>Sets {@link Geobricks#ATTR_CONF_DIR} with the application configuration
 * dir. If the {@link Geobricks#ENV_GEOBRICKS_CONF} environment variable has
 * been set as an existing directory and a subdirectory for this application
 * exists, that subdirectory will be used. If not, a default directory under
 * WEB-INF will be used.</li>
 * <li>Updates the order of the plugin descriptors in
 * {@link Geobricks#ATTR_PLUGINS_DESC} with the order specified in the
 * application configuration.</li>
 * </ul>
 * 
 * @author vicgonco
 * 
 */
@Singleton
public class ApplicationListener implements ServletContextListener {
	private static final String APP_CONF_PATH = "/WEB-INF/conf/gbapp-conf.json";
	private static final Logger logger = Logger
			.getLogger(ApplicationListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			configureContext(sce.getServletContext());
		} catch (Exception e) {
			// The application will not be shown so we need to tell at least
			// the developer
			logger.error("The application could not be loaded", e);
		}
	}

	public void configureContext(ServletContext context) throws IOException {
		PluginDescriptor[] descriptors = (PluginDescriptor[]) context
				.getAttribute(Geobricks.ATTR_PLUGINS_DESC);

		List<String> order = configurePluginConfs(descriptors, context);
		filterAndSortDescriptors(descriptors, order, context);
		configureConfDir(context);
	}

	/**
	 * Configure the {@link Geobricks#ATTR_PLUGINS_CONF} attribute with the
	 * plugin configuration defined for the application.
	 * 
	 * @param descriptors
	 *            Plugin descriptors with the default configurations.
	 * @param context
	 *            Servlet context where the attribute must be set.
	 * @return Ordered list of plugin identifiers, as specified in the
	 *         application configuration file.
	 * @throws IOException
	 *             If any I/O error occurs while reading the application
	 *             configuration file.
	 */
	private List<String> configurePluginConfs(PluginDescriptor[] descriptors,
			ServletContext context) throws IOException {
		InputStream stream = context.getResourceAsStream(APP_CONF_PATH);
		String json = IOUtils.toString(stream);
		stream.close();

		List<String> pluginOrder = new ArrayList<String>();

		// Default plugin configurations when empty
		JSONObject pluginConfigurations = JSONObject.fromObject(json);
		for (Object key : pluginConfigurations.keySet()) {
			String pluginId = key.toString();

			pluginOrder.add(pluginId);

			JSONObject pluginConf = pluginConfigurations
					.getJSONObject(pluginId);
			JSONObject defaultConf = getDefaultConfiguration(descriptors,
					pluginId);
			if (pluginConf.isEmpty() && defaultConf != null) {
				pluginConfigurations.put(pluginId, defaultConf);
			}
		}
		context.setAttribute(Geobricks.ATTR_PLUGINS_CONF, pluginConfigurations);

		return pluginOrder;
	}

	/**
	 * Get the default configuration for the specified plugin.
	 * 
	 * @param descriptors
	 *            All the plugin descriptors.
	 * @param id
	 *            The identifier of the required plugin.
	 * @return The default configuration of the plugin or <code>null</code> if
	 *         the plugin cannot be found.
	 */
	private JSONObject getDefaultConfiguration(PluginDescriptor[] descriptors,
			String id) {
		for (PluginDescriptor descriptor : descriptors) {
			if (descriptor.getId().equals(id)) {
				return descriptor.getDefaultConfiguration();
			}
		}
		return null;
	}

	/**
	 * Sets the {@link Geobricks#ATTR_PLUGINS_DESC} attribute with the ordered
	 * plugin descriptors. It also removes all the plugins that are not defined
	 * in the application descriptor.
	 * 
	 * @param descriptors
	 *            The plugins descriptors.
	 * @param order
	 *            An ordered list of plugin identifiers from the application
	 *            descriptor.
	 * @param context
	 *            The servlet context where the attribute must be set.
	 */
	private void filterAndSortDescriptors(PluginDescriptor[] descriptors,
			final List<String> order, ServletContext context) {
		// Filter
		List<PluginDescriptor> filtered = new ArrayList<PluginDescriptor>();
		for (PluginDescriptor descriptor : descriptors) {
			if (order.contains(descriptor.getId())) {
				filtered.add(descriptor);
			}
		}

		// Sort
		Collections.sort(filtered, new Comparator<PluginDescriptor>() {
			@Override
			public int compare(PluginDescriptor o1, PluginDescriptor o2) {
				return order.indexOf(o1.getId()) - order.indexOf(o2.getId());
			}
		});

		context.setAttribute(Geobricks.ATTR_PLUGINS_DESC,
				filtered.toArray(new PluginDescriptor[filtered.size()]));
	}

	/**
	 * Configures the {@link Geobricks#ATTR_CONF_DIR} attribute with the
	 * application conf directory. ${{@link Geobricks#ENV_GEOBRICKS_CONF}
	 * /&lt;app&gt; or a <i>WEB-INF/default_config</i> if the environment
	 * variable is not set or the directory does not exist.
	 * 
	 * @param context
	 *            The servlet context where the attribute must be set.
	 */
	private void configureConfDir(ServletContext context) {
		String conf = System.getProperty(Geobricks.ENV_GEOBRICKS_CONF);

		// If the system property is not specified, the configuration
		// is directly in the WEB-INF/default_config directory
		String appConf = context.getRealPath("/") + File.separator + "WEB-INF"
				+ File.separator + "default_config";
		if (conf != null && new File(conf).exists()) {
			// If the system property is specified and the app subdirectory
			// exists, the configuration is in GEOBRICKS_CONF_DIR/<app>/
			String appId = getApplicationId(context);
			File appDir = new File(conf, appId);
			if (appDir.exists()) {
				appConf = conf + File.separator + appId;
			}
		}
		context.setAttribute(Geobricks.ATTR_CONF_DIR, appConf);
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
	public String getApplicationId(ServletContext context) {
		String id = context.getInitParameter(Geobricks.INIT_PARAM_ID);
		if (id == null) {
			id = context.getContextPath();
			if (id.startsWith("/")) {
				id = id.substring(1);
			}
		}
		return id;
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// do nothing
	}
}
