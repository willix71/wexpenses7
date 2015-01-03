package w.wexpense.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import w.junit.extras.OrderedSpringJUnit4ClassRunner;
import w.wexpense.model.KlonableTest.KlonableTestBean;
import w.wexpense.test.utils.TestDatabaseConfiguror;
import w.wexpense.test.utils.TestDatabasePopulator;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
@ContextConfiguration(classes= {TestDatabaseConfiguror.class, KlonableTestBean.class})
public class KlonableTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KlonableTest.class);
	
	@Configuration
	public static class KlonableTestBean extends TestDatabasePopulator {
      @Override
      public List<Object> getPopulation() {
         List<Object> population = new ArrayList<Object>();
         Currency chf = new Currency("CHF", "Swiss Francs", 20);
         Currency euro = new Currency("EUR", "Euro", 100);
         population.add(chf);         
         population.add(euro);       
         Country ch = new Country("CH", "Switzerland", chf);         
         Country f = new Country("FR", "France", euro);
         population.add(ch);
         population.add(f);

         population.add(new City(null, "Paris", f));
         population.add(new City("1260", "Nyon", ch));
         population.add(new City("1197", "Prangins", ch));
         
         return population;
      };
	}

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
		Query q = entityManager.createQuery("from " + clazz.getSimpleName()+ " x where x.uid=:uid");
		return (T) q.setParameter("uid", uid).getSingleResult();
	}
}
