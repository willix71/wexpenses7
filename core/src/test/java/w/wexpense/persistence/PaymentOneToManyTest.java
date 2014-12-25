package w.wexpense.persistence;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
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
import w.wexpense.service.model.IPaymentService;

@ActiveProfiles("PaymentOneToManyTest")
@Ignore
public class PaymentOneToManyTest extends AbstractOneToManyTest {

	@Autowired
	private IPaymentService paymentService;
		
	private static Long paymentId;

	private static String expenseUid;

	@Test
	@Order(1)
	public void setup() {
		Assert.assertNotNull(paymentService);
	}

	@Test
	@Order(2)
	public void insertNewExpenseAndPayment() {
		logger.info("================ STEP 1 insert new payment and new expense ================");
		Payment payment = new Payment();
		payment.setFilename("test1");		

		Expense expense1 = newExpense("100");
		expenseUid = expense1.getUid();
		allocate(payment, expense1);

		Payment payment2 = savePayment(payment);

		Assert.assertNotNull("Id has not been set", payment.getId());
		paymentId = payment.getId();
		
		Assert.assertTrue(payment.equals(payment2));
		// instance is the same after insert
		Assert.assertTrue(payment == payment2);

		// need to reload it because of orphanRemoval=true ??? why
		// payment2 = loadPayment(paymentId);
		
		payment2.setFilename("test2");
		Payment payment3 = savePayment(payment2);
		
		Assert.assertTrue(payment.equals(payment3));
		Assert.assertTrue(payment != payment3);

		Assert.assertEquals("test2", payment3.getFilename());

		List<Expense> expenses = payment3.getExpenses();
		Assert.assertEquals(1, expenses.size());
		
		checkNumberOfExpenses(payment.getUid(), 1);
	}

	@Test
	@Order(3)
	public void insertNewExpenseInExistingPayment() {
		logger.info("================ STEP 2 insert new expense in existing payment ================");
		Payment payment = loadPayment(paymentId);
		
		allocate(payment, newExpense("200"));
		Payment payment2 = savePayment(payment);

		Assert.assertTrue(payment.equals(payment2));
		Assert.assertTrue(payment != payment2);

		List<Expense> expenses = payment2.getExpenses();
		Assert.assertEquals(2, expenses.size());
		
		checkNumberOfExpenses(payment.getUid(), 2);
	}

	@Test
	@Order(4)
	public void updateExpenseInExistingPayment() {
		logger.info("================ STEP 2.2 update expense in existing payment ================");
		Payment payment = loadPayment(paymentId);
		List<Expense> expenses = payment.getExpenses();
		expenses.get(0).setDescription("this is a test");
		final String exUid = expenses.get(0).getUid();
		
		savePayment(payment);
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense ex = getByUid(Expense.class, exUid);
				Assert.assertEquals("Expense was not updated", "this is a test", ex.getDescription());

			}
		});	
	}
	
	@Test
	@Order(14)
	public void removeExpenseFromPayment() {
		logger.info("================ STEP 3 remove existing expense from existing payment ================");
		Payment payment = loadPayment(paymentId);
		List<Expense> expenses = payment.getExpenses();
		
		Assert.assertEquals(2, expenses.size());
		deallocate(payment, expenses.get(0), expenses.get(1));
		Payment payment2 = savePayment(payment);

		Assert.assertTrue(payment2.equals(payment));
		Assert.assertTrue(payment2 != payment);

		List<Expense> expenses2 = payment2.getExpenses();
		Assert.assertEquals(0, expenses2.size());

		logger.info("================ STEP 3.1 check expenses are removed ================");
		Expense expense1 = getByUid(Expense.class, expenseUid);
		Assert.assertNotNull(expense1);
		Assert.assertNull(expense1.getPayment());

		checkNumberOfExpenses(payment.getUid(), 0);
	}
	
	@Test
	@Order(15)
	public void insertExistingExpenseInNewPayment() {
		logger.info("================ STEP 4 insert existing payment in new expense ================");
		Payment payment = new Payment();
		payment.setFilename("test4");
		
		Expense expense1 = getByUid(Expense.class, expenseUid);
		allocate(payment, expense1);
		
		Payment payment2 = savePayment(payment);
		
		Assert.assertTrue(payment.equals(payment2));
		Assert.assertTrue(payment == payment2);
		
		checkNumberOfExpenses(payment.getUid(), 1);
	}

	@Test
	@Order(25)
	public void generatingDtasForNewPayment() throws Exception {
		logger.info("================ STEP 6 generating dtas for new payment ================");
		int dtas = ((Number) entityManager.createQuery("select count(a) from PaymentDta a").getSingleResult()).intValue();
		Assert.assertEquals(0, dtas);
		String expenseUid = new TransactionTemplate(transactionManager).execute(new TransactionCallback<String>() {
			@Override
			public String doInTransaction(TransactionStatus status) {
				Expense expense1 = newlines(newExpense("600"));
				persist(expense1);
				
				return expense1.getUid();
			}
		});
		
		Payment payment = new Payment();
		payment.setFilename("dta1");		

		allocate(payment,  getByUid(Expense.class, expenseUid));	
		
		Payment payment2 = paymentService.generatePaymentDtas(payment);
		Assert.assertNotNull("null dta lines", payment2.getDtaLines());
		Assert.assertEquals("dta lines", 4, payment2.getDtaLines().size());
		
	}
	
	@Test
	@Order(26)
	public void generatingDtasForExistingPayment() throws Exception {
		logger.info("================ STEP 7 generating dtas for existing payment ================");
		int dtas = ((Number) entityManager.createQuery("select count(a) from PaymentDta a").getSingleResult()).intValue();
		Assert.assertEquals(4, dtas);
		
		Payment payment = new TransactionTemplate(transactionManager).execute(new TransactionCallback<Payment>() {
			@Override
			public Payment doInTransaction(TransactionStatus status) {
				Payment payment = new Payment();
				payment.setFilename("dta1");		

				Expense expense1 = newlines(newExpense("700"));
				allocate(payment, expense1);
				return savePayment(payment);
			}
		});
		
		Payment payment2 = paymentService.generatePaymentDtas(payment);
		Assert.assertNotNull("null dta lines", payment2.getDtaLines());
		Assert.assertEquals("dta lines", 4, payment2.getDtaLines().size());
		
		paymentId = payment.getId();
	}

	@Test
	@Order(27)
	public void generatingDtasAgainForExistingPayment() throws Exception {
		logger.info("================ STEP 8 generating dtas again for existing payment ================");
		int dtas = ((Number) entityManager.createQuery("select count(a) from PaymentDta a").getSingleResult()).intValue();
		Assert.assertEquals(8, dtas);
		
		Payment payment = loadPayment(paymentId);
		
		Assert.assertEquals("Number of initial expenses", 1, payment.getExpenses().size());
		
		allocate(payment, newlines(newExpense("801")), newlines(newExpense("802")));
		
		Assert.assertEquals("Number of final expenses", 3, payment.getExpenses().size());
		
		Payment payment2 = paymentService.generatePaymentDtas(payment);
		Assert.assertNotNull("null dta lines", payment2.getDtaLines());
		Assert.assertEquals("dta lines", 10, payment2.getDtaLines().size());
		
	}
	
	@Test
	@Order(28)
	public void generatingDtasAgainAfterRemovingPayment() throws Exception {
		logger.info("================ STEP 9 generating dtas after removing existing payment ================");
		int dtas = ((Number) entityManager.createQuery("select count(a) from PaymentDta a").getSingleResult()).intValue();
		Assert.assertEquals(14, dtas);
		
		Payment payment = loadPayment(paymentId);
		
		Assert.assertEquals("Number of initial expenses", 3, payment.getExpenses().size());
		
		deallocate(payment, payment.getExpenses().get(1));
		
		Assert.assertEquals("Number of final expenses", 2, payment.getExpenses().size());
			
		Payment payment2 = paymentService.generatePaymentDtas(payment);
		Assert.assertNotNull("null dta lines", payment2.getDtaLines());
		Assert.assertEquals("dta lines", 7, payment2.getDtaLines().size());
		
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

	void checkNumberOfExpenses(final String uid, final int expectedNumber) {
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Payment p = getByUid(Payment.class, uid);
				Assert.assertEquals("Wrong number of expenses", expectedNumber, p.getExpenses().size());

			}
		});
	}

	Payment allocate(Payment p, Expense... expenses) {
		List<Expense> allocations = p.getExpenses();
		if (allocations == null) {
			allocations = new ArrayList<Expense>();
			p.setExpenses(allocations);
		}
		for (Expense x : expenses) {
			allocations.add(x);
			//x.setPayment(p);
		}
		return p;
	}

	Payment deallocate(Payment p, Expense... expenses) {
		List<Expense> allocations = p.getExpenses();
		for (Expense x : expenses) {
			allocations.remove(x);
			//x.setPayment(null);
		}
		return p;
	}

}
