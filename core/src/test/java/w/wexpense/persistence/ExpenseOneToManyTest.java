package w.wexpense.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.StorableService;
import w.wexpense.utils.ExpenseUtils;

@ActiveProfiles("ConsolidationOneToManyTest")
public class ExpenseOneToManyTest extends AbstractOneToManyTest {

	@Autowired
	@Qualifier("expenseService")
	private StorableService<Expense, Long> expenseService;

	private static Long expenseId;

	@Test
	@Order(1)
	public void setup() {
		Assert.assertNotNull(expenseService);
	}

	@Test
	@Order(2)
	public void insertNewExpense() {
		logger.info("================ STEP 1 insert new expense and new expense ================");
		Expense expense = newExpense("100");
		
		Expense expense2 = saveExpense(expense);

		Assert.assertNotNull("Id has not been set", expense.getId());
		expenseId = expense.getId();
		
		Assert.assertTrue(expense.equals(expense2));
		// instance is the same after insert
		Assert.assertTrue(expense == expense2);
		
		Expense expense3 = saveExpense(expense2);
		
		Assert.assertTrue(expense.equals(expense3));
		Assert.assertTrue(expense != expense3);

		List<TransactionLine> lines = expense3.getTransactions();
		Assert.assertEquals(2, lines.size());
		
		checkNumberOfTransaction(expense.getUid(), 2);
	}

	@Test
	@Order(3)
	public void updateExistingExpense() {
		logger.info("================ STEP 2 modify transaction line in existing expense ================");
		Expense expense = loadExpense(expenseId);
				
		final String trDescription = "test expense";
		expense.getTransactions().get(0).setDescription(trDescription);
		final String trUid = expense.getTransactions().get(0).getUid();
		
		Expense expense2 = saveExpense(expense);

		Assert.assertTrue(expense.equals(expense2));
		Assert.assertTrue(expense != expense2);

		List<TransactionLine> lines = expense2.getTransactions();
		Assert.assertEquals(2, lines.size());
		
		checkNumberOfTransaction(expense.getUid(), 2);
		
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				TransactionLine tr = getByUid(TransactionLine.class, trUid);
				Assert.assertEquals("Transaction line was not updated", trDescription, tr.getDescription());

			}
		});
	}

	@Test
	@Order(4)
	public void removeExistingExpenseTransactionLine() {
		logger.info("================ STEP 3 remove transaction lines from expense ================");
		Expense expense = loadExpense(expenseId);
		
		List<TransactionLine> lines = expense.getTransactions();		
		Assert.assertEquals("number of transcation lines", 2, lines.size());
		TransactionLine line = lines.get(0);
		deallocate(expense, line);
		final String trUid = line.getUid();
		
		ExpenseUtils.addTransactionLine(expense, line.getAccount(), line.getFactor(), line.getAmount().divide(BigDecimal.valueOf(2)));
		ExpenseUtils.addTransactionLine(expense, line.getAccount(), line.getFactor(), line.getAmount().divide(BigDecimal.valueOf(2)));
		
		Expense expense2 = saveExpense(expense);
		Assert.assertTrue(expense.equals(expense2));
		Assert.assertTrue(expense != expense2);

		List<TransactionLine> lines2 = expense2.getTransactions();
		Assert.assertEquals(3, lines2.size());
		
		checkNumberOfTransaction(expense.getUid(), 3);
		
		try {
			getByUid(TransactionLine.class, trUid);
			Assert.fail("Transaction line should not exist!");
		} catch(NoResultException nre) {
			// YES !!!
		}
	}


	//
	// ========================================================================
	//

	Expense saveExpense(final Expense p) {
		return new TransactionTemplate(transactionManager).execute(new TransactionCallback<Expense>() {
			@Override
			public Expense doInTransaction(TransactionStatus status) {
				Expense x = expenseService.save(p);
				x.getTransactions().size();
				return x;
			}
		});
	}

	Expense loadExpense(final Long pid) {
		return new TransactionTemplate(transactionManager).execute(new TransactionCallback<Expense>() {
			@Override
			public Expense doInTransaction(TransactionStatus status) {
				Expense x = expenseService.load(pid);
				x.getTransactions().size();
				return x;
			}
		});
	}

	void checkNumberOfTransaction(final String uid, final int expectedNumber) {
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Expense x = getByUid(Expense.class, uid);
				Assert.assertEquals("Wrong number of transaction lines", expectedNumber, x.getTransactions().size());

			}
		});
	}

	Expense allocate(Expense x, TransactionLine ... lines) {
		List<TransactionLine> allocations = x.getTransactions();
		if (allocations == null) {
			allocations = new ArrayList<TransactionLine>();
			x.setTransactions(allocations);
		}
		for (TransactionLine tl : lines) {
			allocations.add(tl);
			tl.setExpense(x);
		}
		return x;
	}

	Expense deallocate(Expense x, TransactionLine ... lines) {
		List<TransactionLine> allocations = x.getTransactions();
		for (TransactionLine tl : lines) {
			allocations.remove(tl);
			tl.setExpense(null);
		}
		return x;
	}
}
