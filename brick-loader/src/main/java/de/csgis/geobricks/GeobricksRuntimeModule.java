package de.csgis.geobricks;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.Binder;
import com.google.inject.Module;

public class GeobricksRuntimeModule implements Module {

	@Override
	public void configure(Binder binder) {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory(getPersistenceUnitName());
		EntityManager instance = emf.createEntityManager();
		binder.bind(EntityManager.class).toInstance(instance);
		binder.bind(PersistenceUtils.class).toInstance(new PersistenceUtils());
	}

	protected String getPersistenceUnitName() {
		return "geobricks";
	}
}