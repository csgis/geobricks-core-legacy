package org.csgis.geobricks;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

public class GeobricksGuiceServletConfig extends GuiceServletContextListener {

	private class GeobricksServletModule extends ServletModule {
		@Override
		protected void configureServlets() {
			serve("/get").with(TestGetServlet.class);
			serve("/add").with(TestAddServlet.class);
		}
	}

	private class GeobricksRuntimeModule implements Module {
		@Override
		public void configure(Binder binder) {
			EntityManagerFactory emf = Persistence
					.createEntityManagerFactory("geobricks");
			EntityManager instance = emf.createEntityManager();
			binder.bind(EntityManager.class).toInstance(instance);
		}
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new GeobricksServletModule(),
				new GeobricksRuntimeModule());
	}

}