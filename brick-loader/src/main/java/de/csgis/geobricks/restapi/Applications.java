package de.csgis.geobricks.restapi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.Application_;

public class Applications {

	@Inject
	private EntityManager entityManager;

	@Inject
	private Provider<ApplicationPlugins> applicationPluginsProvider;

	public Application get(String appId) throws ApplicationNotFoundException {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Application> criteria = criteriaBuilder
				.createQuery(Application.class);
		criteria.where(criteriaBuilder.equal(criteria.from(Application.class)
				.get(Application_.id), appId));
		List<Application> list = entityManager.createQuery(criteria)
				.getResultList();

		if (list.size() != 0) {
			return list.get(0);
		} else {
			throw new ApplicationNotFoundException(appId);
		}
	}

	public void put(String appId) {
		try {
			get(appId);
		} catch (ApplicationNotFoundException e) {
			Application app = new Application();
			app.setId(appId);

			entityManager.getTransaction().begin();
			entityManager.persist(app);
			entityManager.getTransaction().commit();
		}
	}

	public void delete(String appId) throws ApplicationNotFoundException {
		Application app = get(appId);

		entityManager.getTransaction().begin();
		entityManager.remove(app);
		entityManager.getTransaction().commit();
	}

	public ApplicationPlugins getApplicationPlugins(String appId)
			throws ApplicationNotFoundException {
		ApplicationPlugins applicationPlugins = applicationPluginsProvider
				.get();
		applicationPlugins.setApplication(appId);
		return applicationPlugins;
	}
}
