package de.csgis.geobricks.guice;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PersistenceUtils;
import de.csgis.geobricks.PluginRegistry;

public class RuntimeModule implements Module {
	private static final String PERSISTENCE_CONF = "persistence.conf";

	@Override
	public void configure(Binder binder) {
		try {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(
					getPersistenceUnitName(), getConnectionProperties());
			EntityManager instance = emf.createEntityManager();
			binder.bind(EntityManager.class).toInstance(instance);
			binder.bind(PersistenceUtils.class).toInstance(
					PersistenceUtils.getInstance());
			binder.bind(PluginRegistry.class).toInstance(new PluginRegistry());
		} catch (IOException e) {
			throw new RuntimeException(
					"Cannot read connection configuration file.", e);
		}
	}

	protected Map<String, String> getConnectionProperties() throws IOException {
		String confPath = System.getProperty("GEOBRICKS_CONF_DIR");
		if (confPath == null) {
			confPath = Geobricks.DEFAULT_GEOBRICKS_CONF_DIR;
		}

		File confDir = new File(confPath);
		if (!confDir.exists()) {
			throw new RuntimeException(
					"Configuration directory does not exist: "
							+ confDir.getAbsolutePath());
		}

		File connectionConfig = new File(confDir, PERSISTENCE_CONF);
		if (!connectionConfig.exists()) {
			throw new RuntimeException(
					"Persistence configuration file does not exist: "
							+ connectionConfig.getAbsolutePath());
		}

		Properties properties = new Properties();
		properties.load(new FileReader(connectionConfig));

		Map<String, String> ret = new HashMap<String, String>();
		for (Object key : properties.keySet()) {
			ret.put(key.toString(), properties.getProperty(key.toString()));
		}
		return ret;
	}

	protected String getPersistenceUnitName() {
		return "geobricks";
	}
}