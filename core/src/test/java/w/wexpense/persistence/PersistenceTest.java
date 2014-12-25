package w.wexpense.persistence;

import static w.wexpense.model.enums.TransactionLineEnum.OUT;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import w.junit.extras.OrderedSpringJUnit4ClassRunner;
import w.wexpense.model.Account;
import w.wexpense.model.Currency;
import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.utils.ExpenseUtils;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
@ContextConfiguration(locations={"classpath:persistence-test-context.xml"})
public class PersistenceTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private DatabasePopulator populator;
	
	@Autowired 
	private PlatformTransactionManager transactionManager;
	
	@Test
	@Order(1)
	public void setup() {
		Assert.assertNotNull(entityManager);
		//Assert.assertNotNull(populator);
		populator.populate();
	}
	
	@Test
	@Order(2)
	@Transactional
	public void testSetup() {
		Query q = entityManager.createQuery("from Currency");		
		Assert.assertEquals(4, q.getResultList().size());	
	}
	
//	@Test
//	@Transactional
//	public void testOrderByEntity() {
//		Query q = entityManager.createQuery("from Expense");
//		Expense x = (Expense) q.getResultList().get(0);
//		System.out.println(x);		
//		System.out.println(x.getTransactions());
//	}
//	
//	@Test
//	@Transactional
//	public void testOrderBy() {
//		Query q = entityManager.createQuery("from Payment");
//		Payment p = (Payment) q.getResultList().get(0);
//		System.out.println(p);		
//		System.out.println(p.getExpenses());
//		System.out.println(p.getDtaLines());
//	}
	
	
	
	@Test
	@Order(10)
	@Ignore
	public void testOneToMany() {
		BigDecimal amount = new BigDecimal("12.0");
				
		Query q = entityManager.createQuery("from Currency c where c.code = 'CHF' ");
		Currency chf = (Currency) q.getSingleResult();		
		Assert.assertNotNull("no CHF currency", chf);
		
		q = entityManager.createQuery("from Account a where a.name = 'cash' ");
		Account cashAccount = (Account) q.getSingleResult();		
		Assert.assertNotNull("no cash account", cashAccount);		
		
		// create a payee
		final Payee payee = new Payee();
		payee.setName("Test");
		
		// create an expense
		final Expense expense1 = new Expense();
		expense1.setDate(new Date());
		expense1.setAmount(amount);
		expense1.setPayee(payee);
		expense1.setCurrency(chf);
		
		ExpenseUtils.addTransactionLine(expense1, cashAccount, TransactionLineEnum.OUT, amount).updateValue();
        ExpenseUtils.addTransactionLine(expense1, cashAccount, TransactionLineEnum.IN, amount).updateValue();
		// save them both
		persist(payee, expense1);
				
		// verify creation
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense ex = getByUid(Expense.class, expense1.getUid());
				Assert.assertNotNull("no expense found", ex);
				
				// make sure instance are different
				Assert.assertFalse("Same instance", ex == expense1);
				Assert.assertTrue("Same expense", ex.equals(expense1));
				
				Assert.assertEquals(2, ex.getTransactions().size());
			}
		});
		
		// create and save a transaction line
		final TransactionLine tl1 = buildTransactionLine(cashAccount, OUT, expense1);
		persist(tl1);
		
		// verify creation
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense ex = getByUid(Expense.class, expense1.getUid());

				Assert.assertEquals(3, ex.getTransactions().size());
				Assert.assertEquals(tl1.getUid(), tl1.getUid());
			}
		});
		
		// create but dont save the transaction line first
		final TransactionLine tl2 = buildTransactionLine(cashAccount, OUT, new BigDecimal("11"));
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense ex = getByUid(Expense.class, expense1.getUid());								
				ex.getTransactions().add(tl2);
				tl2.setExpense(ex);
			}
		});
		
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				TransactionLine tl = getByUid(TransactionLine.class, tl2.getUid());				
				Assert.assertNotNull(tl);
				Assert.assertNotNull(tl.getExpense());
			}
		});
		
		// create but dont save the transaction line
		final TransactionLine tl3 = buildTransactionLine(cashAccount, OUT, new BigDecimal("33"));
		
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense ex = getByUid(Expense.class, expense1.getUid());								
				ex.getTransactions().add(tl3);
				tl3.setExpense(ex);
			}
		});
		
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				TransactionLine tl = getByUid(TransactionLine.class, tl3.getUid());				
				Assert.assertNotNull(tl);
				Assert.assertNotNull(tl.getExpense());
			}
		}); 
	}
	
	
	
	//
	// ========================================================================
	//
	
	void persist(final Object ... objs) {
		 new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				for(Object o: objs) {
					entityManager.persist(o);
				}
			}
		});
	}
	
	<T> T merge(final T t) {
		return new TransactionTemplate(transactionManager).execute(new TransactionCallback<T>() {
			@Override
			public T doInTransaction(TransactionStatus status) {
				return entityManager.merge(t);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	<T> T getByUid(Class<T> clazz, String uid) {
		Query q = entityManager.createQuery("from " + clazz.getSimpleName()+ " x where x.uid=:uid");
		return (T) q.setParameter("uid", uid).getSingleResult();
	}
	
	TransactionLine buildTransactionLine(Account account, TransactionLineEnum factor, Expense expense) {
		TransactionLine tl = buildTransactionLine(account, factor, expense.getAmount());
		tl.setExpense(expense);
		return tl;
	}
	
	TransactionLine buildTransactionLine(Account account, TransactionLineEnum factor, BigDecimal amount) {
		TransactionLine tl = new TransactionLine();
		tl.setFactor(factor);
		tl.setAccount(account);
		tl.setAmount(amount);
		tl.updateValue();
		return tl;
	}
}
