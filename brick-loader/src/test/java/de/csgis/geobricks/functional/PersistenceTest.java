package de.csgis.geobricks.functional;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Test;

import de.csgis.geobricks.model.Installation;

public class PersistenceTest {

	@Test
	public void testPersistenceUnit() throws Exception {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("test");
		EntityManager em = emf.createEntityManager();
		Installation app = new Installation();
		app.setId("boh!");

		em.getTransaction().begin();
		em.persist(app);
		em.getTransaction().commit();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Installation> criteriaQuery = cb
				.createQuery(Installation.class);
		criteriaQuery.from(Installation.class);
		List<Installation> list = em.createQuery(criteriaQuery).getResultList();
		assertEquals(1, list.size());

		em.close();
	}

}
