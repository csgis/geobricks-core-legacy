package de.csgis.geobricks;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.Application_;
import de.csgis.geobricks.model.Plugin;
import de.csgis.geobricks.model.Plugin_;

public class PersistenceUtils {
	@Inject
	private EntityManager em;

	PersistenceUtils() {
	}

	public Application getApplication(String name) {
		return getEntityById(Application.class, Application_.id, name);
	}

	public Plugin getPlugin(String name) {
		return getEntityById(Plugin.class, Plugin_.id, name);
	}

	private <T extends Object> T getEntityById(Class<T> clazz,
			SingularAttribute<T, String> idAttribute, String id) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = criteriaBuilder.createQuery(clazz);
		Root<T> root = criteria.from(clazz);
		Predicate predicate = criteriaBuilder.equal(root.get(idAttribute), id);
		criteria.where(predicate);

		TypedQuery<T> query = em.createQuery(criteria);
		return query.getSingleResult();
	}
}
