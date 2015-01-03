package w.wexpense.persistence;

import static w.wexpense.model.enums.TransactionLineEnum.OUT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import w.junit.extras.OrderedSpringJUnit4ClassRunner;
import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.persistence.PersistenceTest.PersistenceTestConfiguror;
import w.wexpense.test.utils.PersistenceHelper;
import w.wexpense.test.utils.TestDatabaseConfiguror;
import w.wexpense.test.utils.TestDatabasePopulator;
import w.wexpense.test.utils.TestServiceConfiguror;
import w.wexpense.utils.ExpenseUtils;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
@ContextConfiguration(classes= {TestDatabaseConfiguror.class, PersistenceTestConfiguror.class, TestServiceConfiguror.class})
public class PersistenceTest {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired 
	private PlatformTransactionManager transactionManager;
	
   protected PersistenceHelper persistenceHelper;

   @PostConstruct
   public void postConstuct() {
      persistenceHelper = new PersistenceHelper(entityManager, transactionManager);            
   }
   
   @Configuration
   public static class PersistenceTestConfiguror extends TestDatabasePopulator {
      @Override
      public List<Object> getPopulation() {
         List<Object> population = new ArrayList<Object>();
         Currency chf = new Currency("CHF", "Swiss Francs", 20);
         population.add(chf);
         Currency euro = new Currency("EUR", "Euro", 100);
         population.add(euro);
         Currency usd = new Currency("USD", "US Dollar", 100);
         population.add(usd);
         Currency gbp = new Currency("GBP", "British Pounds", 100);
         population.add(gbp);

         Country ch = new Country("CH", "Switzerland", chf);
         population.add(ch);
         Country f = new Country("FR", "France", euro);
         population.add(f);
         population.add(new Country("IT", "Italie", euro));
         population.add(new Country("DE", "Germany", euro));
         population.add(new Country("US", "United States of America", usd));

         City paris = new City(null, "Paris", f);
         population.add(paris);
         City nyon = new City("1260", "Nyon", ch);
         population.add(nyon);
         City prangins = new City("1197", "Prangins", ch);
         population.add(prangins);

         return population;
      };
   }
   
	@Test
	@Order(1)
	public void setup() {
		Assert.assertNotNull(entityManager);
		Assert.assertNotNull(persistenceHelper);
	}
	
	@Test
	@Order(2)
	@Transactional
	public void testSetup() {
		Query q = entityManager.createQuery("from Currency");		
		Assert.assertEquals(4, q.getResultList().size());	
	}
	
	@Test
	@Order(10)
	@Ignore
	public void testOneToMany() {
		BigDecimal amount = new BigDecimal("12.0");
				
		final Country ch = persistenceHelper.get(Country.class, "CH");    
      Assert.assertNotNull("no CH country", ch);
      
		final Account cashAccount = persistenceHelper.getByName(Account.class, "cash");		
		Assert.assertNotNull("no cash account", cashAccount);		
		
		final City city = new City("123","somewhere", ch);
		
		final Payee payee = new Payee();
		payee.setName("Test");
		payee.setCity(city);
		
		// create an expense
		final Expense expense1 = ExpenseUtils.newExpense(new Date(), amount, payee, ch.getCurrency(), cashAccount, cashAccount);

		// save them all
		persistenceHelper.persistInTx(city, payee, expense1);
				
		// verify creation
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense ex = persistenceHelper.getByUid(Expense.class, expense1.getUid());
				Assert.assertNotNull("no expense found", ex);
				
				// make sure instance are different
				Assert.assertFalse("Same instance", ex == expense1);
				Assert.assertTrue("Same expense", ex.equals(expense1));
				
				Assert.assertEquals(2, ex.getTransactions().size());
			}
		});
		
		// create and save a transaction line
		final TransactionLine tl1 = ExpenseUtils.newTransactionLine(expense1, cashAccount, OUT);
		persistenceHelper.persistInTx(tl1);
		
		// verify creation
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense ex = persistenceHelper.getByUid(Expense.class, expense1.getUid());

				Assert.assertEquals(3, ex.getTransactions().size());
			}
		});
		
		// create but don't save the transaction line first
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense ex = persistenceHelper.getByUid(Expense.class, expense1.getUid());
				ExpenseUtils.newTransactionLine(ex, cashAccount, OUT);
			}
		});
		
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense ex = persistenceHelper.getByUid(Expense.class, expense1.getUid());
				Assert.assertEquals(4, ex.getTransactions().size());
			}
		});
	}
	
}
