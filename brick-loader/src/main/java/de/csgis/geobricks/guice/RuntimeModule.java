package de.csgis.geobricks.guice;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.csgis.geobricks.PersistenceUtils;
import de.csgis.geobricks.PluginRegistry;

public class RuntimeModule implements Module {

	@Override
	public void configure(Binder binder) {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory(getPersistenceUnitName());
		EntityManager instance = emf.createEntityManager();
		binder.bind(EntityManager.class).toInstance(instance);
		binder.bind(PersistenceUtils.class).toInstance(
				PersistenceUtils.getInstance());
		binder.bind(PluginRegistry.class).toInstance(new PluginRegistry());
	}

	protected String getPersistenceUnitName() {
		return "geobricks";
	}
}