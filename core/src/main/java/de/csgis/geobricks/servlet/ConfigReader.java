package de.csgis.geobricks.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import de.csgis.geobricks.Geobricks;

public class ConfigReader {
	private static final Logger logger = Logger.getLogger(ConfigReader.class);

	private ServletContext context;

	public ConfigReader(ServletContext context) throws IOException {
		this.context = context;
		updateAppProperties();
	}

	public String getConfigDir() {
		return context.getAttribute(Geobricks.ATTR_CONF_DIR).toString();
	}

	public Properties getAppProperties() {
		Object lastAccessedObj = context
				.getAttribute(Geobricks.ATTR_APP_PROPERTIES_LAST_ACCESSED);
		Object appPropertiesObj = context
				.getAttribute(Geobricks.ATTR_APP_PROPERTIES);

		boolean needsUpdate = !(lastAccessedObj instanceof Long)
				|| appPropertiesObj == null;
		if (lastAccessedObj instanceof Long) {
			long lastAccessed = (long) lastAccessedObj;
			File file = new File(context.getAttribute(Geobricks.ATTR_CONF_DIR)
					.toString(), "app.properties");
			long lastModified = file.lastModified();
			needsUpdate |= lastModified > lastAccessed;
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

	private void updateAppProperties() throws IOException {
		File file = new File(context.getAttribute(Geobricks.ATTR_CONF_DIR)
				.toString(), "app.properties");
		Properties appProperties = new Properties();
		if (file.exists()) {
			FileInputStream in = new FileInputStream(file);
			appProperties.load(in);
			in.close();
		}
		context.setAttribute(Geobricks.ATTR_APP_PROPERTIES, appProperties);
		context.setAttribute(Geobricks.ATTR_APP_PROPERTIES_LAST_ACCESSED,
				new Long(System.currentTimeMillis()));
	}
}
