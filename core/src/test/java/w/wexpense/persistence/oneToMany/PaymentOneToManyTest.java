package w.wexpense.persistence.oneToMany;

import java.util.List;

import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.model.IPaymentService;

@ActiveProfiles("PaymentOneToManyTest")
public class PaymentOneToManyTest extends AbstractOneToManyTest {

	@Autowired
	private IPaymentService paymentService;
		
	private static final Payment PAYMENT = new Payment();

	@Test
	@Order(1)
	public void setup() {
		Assert.assertNotNull(paymentService);
	}

	@Test
	@Order(2)
	public void insertNewExpenseAndPayment() {
		logger.info("================ STEP 1 insert new payment and new expense ================");
		
		PAYMENT.setFilename("test1");		

		Expense expense1 = newExpense("100");
		PAYMENT.addExpense(expense1);

		Payment payment2 = savePayment(PAYMENT);

		Assert.assertNotNull("Id has not been set", PAYMENT.getId());
		
      Assert.assertEquals(PAYMENT,payment2);
      Assert.assertSame(PAYMENT, payment2);
		
		payment2.setFilename("test2");
		
		Payment payment3 = savePayment(payment2);
		
      Assert.assertEquals(PAYMENT, payment3);
      Assert.assertNotSame(PAYMENT, payment3);

		Assert.assertEquals("test2", payment3.getFilename());

		List<Expense> expenses = payment3.getExpenses();
		Assert.assertEquals(1, expenses.size());
		
		checkNumberOfExpenses(PAYMENT.getUid(), 1);
		
		Assert.assertEquals(1, persistenceHelper.count(Expense.class));
		Assert.assertEquals(2, persistenceHelper.count(TransactionLine.class));
	}

	@Test
	@Order(3)
	public void insertNewExpenseInExistingPayment() {
		logger.info("================ STEP 2 insert new expense in existing payment ================");
		Payment payment2 = loadPayment(PAYMENT.getId());
		
		Expense newExpense = newExpense("200");
		payment2.addExpense(newExpense);
		
		Payment payment3 = savePayment(payment2);

      Assert.assertEquals(PAYMENT, payment3);
      Assert.assertNotSame(PAYMENT, payment3);

		List<Expense> expenses = payment3.getExpenses();
		Assert.assertEquals(2, expenses.size());
		
		checkNumberOfExpenses(PAYMENT.getUid(), 2);
		
	    Assert.assertEquals(2, persistenceHelper.count(Expense.class));
	    Assert.assertEquals(4, persistenceHelper.count(TransactionLine.class));
	}

	@Test
	@Order(4)
	public void updateExpenseInExistingPayment() {
		logger.info("================ STEP 2.2 update expense in existing payment ================");
		
		checkNumberOfExpenses(PAYMENT.getUid(), 2);
		
		Payment payment = loadPayment(PAYMENT.getId());
		List<Expense> expenses = payment.getExpenses();
		Assert.assertEquals(2, expenses.size());
		
		final Expense expense = expenses.get(0);
		expense.setDescription("this is a test2");
		
		savePayment(payment);
		
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense ex = persistenceHelper.getByUid(Expense.class, expense.getUid());
				Assert.assertEquals("Expense was not updated", "this is a test2", ex.getDescription());
			}
		});	
	}
	
	@Test
	@Order(5)
	public void removeExpenseFromPayment() {
		logger.info("================ STEP 3 remove existing expense from existing payment ================");
		checkNumberOfExpenses(PAYMENT.getUid(), 2);
		
		Payment payment2 = loadPayment(PAYMENT.getId());
		List<Expense> expenses = payment2.getExpenses();
		Assert.assertEquals("number of expenses", 2, expenses.size());
		
		final Expense expense = expenses.get(0);
		payment2.removeExpense(expense);
		
		Payment payment3 = savePayment(payment2);

		Assert.assertEquals(payment2,payment3);
		Assert.assertNotSame(payment2,payment3);

		List<Expense> expenses3 = payment3.getExpenses();
		Assert.assertEquals(1, expenses3.size());

		logger.info("================ STEP 3.1 check expenses are removed ================");
		Expense expense1 = persistenceHelper.getByUid(Expense.class, expense.getUid());
		Assert.assertNotNull(expense1);
		Assert.assertNull(expense1.getPayment());

		checkNumberOfExpenses(PAYMENT.getUid(), 1);
	}

   //
   // ========================================================================
   //
	
	@Test
	@Order(25)
	public void generatingDtasForNewPayment() throws Exception {
		logger.info("================ STEP 6 generating dtas for new payment ================");
		int dtas = persistenceHelper.count(PaymentDta.class);
		Assert.assertEquals(0, dtas);
		
		Payment payment = new Payment();
		payment.setFilename("dta6");
		
		Expense expense = newExpense("600");
		payment.addExpense(expense);	
		
		Payment payment2 = paymentService.generatePaymentDtas(payment);
		Assert.assertNotNull("null dta lines", payment2.getDtaLines());
		Assert.assertEquals("dta lines", 4, payment2.getDtaLines().size());	
	}
	
	private static final Payment dta_payment = new Payment();
	@Test
	@Order(26)
	public void generatingDtasForExistingPayment() throws Exception {
		logger.info("================ STEP 7 generating dtas for existing payment ================");
		int dtas = persistenceHelper.count(PaymentDta.class);
		Assert.assertEquals(4, dtas);
				
		dta_payment.setFilename("dta7");
		dta_payment.addExpense(newExpense("700"));  
      savePayment(dta_payment);
		
      Payment payment2 = loadPayment(dta_payment.getId());
      
      // make sure it has been relaoded
      Assert.assertNotSame(dta_payment, payment2);
      
		Payment payment3 = paymentService.generatePaymentDtas(payment2);
		Assert.assertNotNull("null dta lines", payment3.getDtaLines());
		Assert.assertEquals("dta lines", 4, payment3.getDtaLines().size());
		
      dtas = persistenceHelper.count(PaymentDta.class);
      Assert.assertEquals(8, dtas);
	}

	@Test
	@Order(27)
	public void generatingDtasAgainForExistingPayment() throws Exception {
		logger.info("================ STEP 8 generating dtas again for existing payment ================");
      int dtas = persistenceHelper.count(PaymentDta.class);
      Assert.assertEquals(8, dtas);
		
		Payment payment = loadPayment(dta_payment.getId());
		
		Assert.assertEquals("Number of initial expenses", 1, payment.getExpenses().size());
		
		payment.addExpense(newExpense("801"));
		payment.addExpense(newExpense("802"));
		
		Assert.assertEquals("Number of final expenses", 3, payment.getExpenses().size());
		
		Payment payment2 = paymentService.generatePaymentDtas(payment);
		Assert.assertNotNull("null dta lines", payment2.getDtaLines());
		Assert.assertEquals("dta lines", 10, payment2.getDtaLines().size());
	
		dtas = persistenceHelper.count(PaymentDta.class);
      Assert.assertEquals(14, dtas);
	}
	
	@Test
	@Order(28)
	public void generatingDtasAgainAfterRemovingPayment() throws Exception {
		logger.info("================ STEP 9 generating dtas after removing existing payment ================");
		int dtas = persistenceHelper.count(PaymentDta.class);
		Assert.assertEquals(14, dtas);
		
		Payment payment = loadPayment(dta_payment.getId());
		
		Assert.assertEquals("Number of initial expenses", 3, payment.getExpenses().size());
		
		payment.removeExpense(payment.getExpenses().get(1));
		
		Assert.assertEquals("Number of final expenses", 2, payment.getExpenses().size());
			
		Payment payment2 = paymentService.generatePaymentDtas(payment);
		Assert.assertNotNull("null dta lines", payment2.getDtaLines());
		Assert.assertEquals("dta lines", 7, payment2.getDtaLines().size());
		
		dtas = persistenceHelper.count(PaymentDta.class);
      Assert.assertEquals(11, dtas);
	}
	
	//
   // ========================================================================
   //
	
	@Test
	@Order(30)
	public void testNextPayment() {	   
	   Assert.assertEquals(1, countSelectable());
	   
	   Payment p1 = nextPayment();
	   Assert.assertNotNull(p1);
	   
	   Assert.assertEquals(PAYMENT.getUid(), p1.getUid());
	   
	   p1.setSelectable(false);
	   savePayment(p1);
	   
	   Assert.assertEquals(0, countSelectable());
	   
	   Payment p2 = paymentService.getNextPayment();
	   Assert.assertNotNull(p2);
	   
	   Assert.assertEquals(1, countSelectable());
	   
	   Assert.assertFalse(PAYMENT.getUid().equals(p2.getUid()));
	   
	   Payment p3  = paymentService.getNextPayment();
      Assert.assertNotNull(p3);
      
      Assert.assertEquals(1, countSelectable());
      
      Assert.assertEquals(p2.getUid(), p3.getUid());
      
      // insert a new selectable payment
      Payment p4 = savePayment(new Payment());
      Assert.assertEquals(2, countSelectable());
      
      Payment p5 = paymentService.getNextPayment();
      Assert.assertNotNull(p5);
      
      Assert.assertEquals(2, countSelectable());
      
      Assert.assertFalse(p4.getUid().equals(p5.getUid()));
      Assert.assertEquals(p5.getUid(), p2.getUid());
	}

	private int countSelectable() {
	   Query q = entityManager.createQuery("select count(*) from Payment p where p.selectable = true");
	   return ((Number) q.getSingleResult()).intValue();
	}
	
	//
	// ========================================================================
	//

	Payment savePayment(final Payment p) {
		return new TransactionTemplate(transactionManager).execute(new TransactionCallback<Payment>() {
			@Override
			public Payment doInTransaction(TransactionStatus status) {
				Payment p2 = paymentService.save(p);
				p2.getExpenses().size();
				return p2;
			}
		});
	}

	Payment loadPayment(final Long pid) {
		return new TransactionTemplate(transactionManager).execute(new TransactionCallback<Payment>() {
			@Override
			public Payment doInTransaction(TransactionStatus status) {
				Payment p2 = paymentService.load(pid);
				p2.getExpenses().size();
				p2.getDtaLines().size();
				return p2;
			}
		});
	}

  Payment nextPayment() {
      return new TransactionTemplate(transactionManager).execute(new TransactionCallback<Payment>() {
         @Override
         public Payment doInTransaction(TransactionStatus status) {
            Payment p2 = paymentService.getNextPayment();
            p2.getExpenses().size();
            p2.getDtaLines().size();
            return p2;
         }
      });
   }
  
	void checkNumberOfExpenses(final String uid, final int expectedNumber) {
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Payment p = persistenceHelper.getByUid(Payment.class, uid);
				Assert.assertEquals("Wrong number of expenses", expectedNumber, p.getExpenses().size());

			}
		});
	}

}
