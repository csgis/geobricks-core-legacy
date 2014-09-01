package de.csgis.geobricks.config;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class JarPluginScanner implements PluginScanner {
	private static final Logger logger = Logger
			.getLogger(JarPluginScanner.class);

	private Set<String> entries;
	private JSONObject pluginConf;

	private Iterator<String> iterator;

	private ServletContext context;

	public void init(ServletContext context) {
		this.context = context;
		this.iterator = context.getResourcePaths("/WEB-INF/lib").iterator();
	}

	public boolean next() {
		if (!iterator.hasNext()) {
			return false;
		}

		entries = new HashSet<String>();
		pluginConf = null;

		while (pluginConf == null && iterator.hasNext()) {
			String resource = iterator.next();
			ZipInputStream jar = new ZipInputStream(
					context.getResourceAsStream(resource));
			try {
				processJar(jar);
			} catch (IOException e) {
				logger.error("Cannot read jar: " + jar + ". Ignoring.", e);
			} finally {
				try {
					jar.close();
				} catch (IOException e) {
				}
			}
		}

		return pluginConf != null;
	}

	private void processJar(ZipInputStream jar) throws IOException {
		ZipEntry entry;
		while ((entry = jar.getNextEntry()) != null) {
			String name = entry.getName();
			if (name.matches(PluginListener.modulesDir + File.separator + ".+")) {
				entries.add(name);
			} else if (name.matches("geobricks" + File.separator
					+ "[^/]+.pluginconf")) {
				pluginConf = JSONObject.fromObject(IOUtils.toString(jar));
			}
		}
	}

	@Override
	public Set<String> entries() {
		return entries;
	}

	@Override
	public JSONObject pluginConf() {
		return pluginConf;
	}
}
