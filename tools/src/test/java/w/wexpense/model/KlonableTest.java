package w.wexpense.model;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import w.wexpense.test.config.AbstractTest;
import w.wexpense.test.config.DatabasePopulationConfig;

@Configuration
class KlonableTestConfig extends DatabasePopulationConfig {
	public KlonableTestConfig() {
		Currency chf = add(new Currency("CHF", "Swiss Francs", 20));
		Currency euro = add(new Currency("EUR", "Euro", 100));
		Country ch = add(new Country("CH", "Switzerland", chf));
		Country f = add(new Country("FR", "France", euro));

		add(new City(null, "Paris", f));
		add(new City("1260", "Nyon", ch));
		add(new City("1197", "Prangins", ch));
	};
}

@ContextConfiguration(classes = { KlonableTestConfig.class })
public class KlonableTest extends AbstractTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(KlonableTest.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@Order(0)
	public void setup() {
		Assert.assertNotNull(entityManager);
		LOGGER.info("\n\n==================Setup done==================\n");
	}

	@Test
	@Order(1)
	public void testCountCities1() {
		List<City> cities = getAll(City.class);
		Assert.assertEquals(3, cities.size());
	}

	@Test
	@Order(2)
	@Transactional
	public void duplicateCity() {
		Query q = entityManager.createQuery("from City x where x.zip=:zip");
		City prangins1197 = (City) q.setParameter("zip", "1197").getSingleResult();

		City prangins11971 = prangins1197.duplicate();
		Assert.assertFalse(prangins11971.equals(prangins1197));

		prangins11971.setZip("11971");
		entityManager.persist(prangins11971);
	}

	@Test
	@Order(3)
	public void testCountCities2() {
		List<City> cities = getAll(City.class);
		Assert.assertEquals(4, cities.size());
	}

	@Test
	@Order(4)
	@Transactional
	public void checkCountries() {
		Query q = entityManager.createQuery("from City x where x.zip=:zip");
		City prangins1197 = (City) q.setParameter("zip", "1197").getSingleResult();
		City prangins11971 = (City) q.setParameter("zip", "11971").getSingleResult();

		Assert.assertFalse(prangins11971.equals(prangins1197));
	}

	@SuppressWarnings("unchecked")
	<T> List<T> getAll(Class<T> clazz) {
		Query q = entityManager.createQuery("from " + clazz.getSimpleName());
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	<T> T getByUid(Class<T> clazz, String uid) {
		Query q = entityManager.createQuery("from " + clazz.getSimpleName() + " x where x.uid=:uid");
		return (T) q.setParameter("uid", uid).getSingleResult();
	}
}
