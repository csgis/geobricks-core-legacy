package de.csgis.geobricks.guice;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.csgis.geobricks.config.JarPluginScanner;
import de.csgis.geobricks.config.PluginScanner;

public class TestResourceScanner implements PluginScanner {
	private static final Logger logger = Logger
			.getLogger(TestResourceScanner.class);

	private Set<String> entries;
	private JSONObject pluginConf;
	private JarPluginScanner delegate;

	@Override
	public void init(ServletContext context) {
		URL location = getClass().getResource("/");
		if (location.getProtocol().equals("jar")) {
			delegate = new JarPluginScanner();
			delegate.init(context);
		}
	}

	@Override
	public boolean next() {
		if (delegate != null) {
			return delegate.next();
		} else {
			boolean hasNext = pluginConf == null;
			if (hasNext) {
				scanDir(new File(getClass().getResource("/").getPath()));
			}
			return hasNext;
		}
	}

	public void scanDir(File root) {
		entries = new HashSet<String>();

		File[] confs = new File(root, "conf").listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().matches(
						"[^" + File.separator + "]+-pluginconf.json");
			}
		});

		for (File conf : confs) {
			try {
				String content = IOUtils.toString(new FileInputStream(conf));
				pluginConf = JSONObject.fromObject(content);
			} catch (IOException e) {
				logger.error(
						"Cannot read plugin conf file: "
								+ conf.getAbsolutePath() + ". Ignoring.", e);
			}
		}

		String path = "webapp" + File.separator + "modules";
		File[] moduleFiles = new File(root, path).listFiles();
		for (File file : moduleFiles) {
			entries.add(path + File.separator + file.getName());
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
