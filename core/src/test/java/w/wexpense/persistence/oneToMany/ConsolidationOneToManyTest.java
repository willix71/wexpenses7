package w.wexpense.persistence.oneToMany;

import java.util.Date;
import java.util.List;

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

import w.wexpense.model.Consolidation;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.StorableService;

@ActiveProfiles("ConsolidationOneToManyTest")
public class ConsolidationOneToManyTest extends AbstractOneToManyTest {

    @Autowired
    @Qualifier("expenseService")
    protected StorableService<Expense, Long> expenseService;
   
	@Autowired
	@Qualifier("consolidationService")
	private StorableService<Consolidation, Long> consolidationService;

	private static final Consolidation CONSOLIDATION = new Consolidation();

	@Test
	@Order(1)
	public void setup() {
	   Assert.assertNotNull(expenseService);
		Assert.assertNotNull(consolidationService);
	}

	@Test
	@Order(2)
	public void insertNewConsolidation() {
		logger.info("================ STEP 1 insert new consolidation and new expense ================");

		CONSOLIDATION.setInstitution(testPayee());
		CONSOLIDATION.setDate(new Date());

		// we need to save it first because consolidation deal with transaction lines and not expenses
		Expense expense = expenseService.save(newExpense("100"));		
		CONSOLIDATION.addTransaction(expense.getTransactions().get(0));
		
		Consolidation consolidation2 = saveConsolidation(CONSOLIDATION);

		Assert.assertNotNull("Id has not been set", CONSOLIDATION.getId());
		
		Assert.assertEquals(CONSOLIDATION,consolidation2);
		Assert.assertSame(CONSOLIDATION, consolidation2);
		
		Consolidation consolidation3 = saveConsolidation(consolidation2);
		
		Assert.assertEquals(CONSOLIDATION,consolidation3);
      Assert.assertNotSame(CONSOLIDATION, consolidation3);

		List<TransactionLine> lines = consolidation3.getTransactions();
		Assert.assertEquals(1, lines.size());
		
		checkNumberOfTransaction(CONSOLIDATION.getUid(), 1);
		
		Assert.assertEquals(2, persistenceHelper.count(TransactionLine.class));
	}

	@Test
	@Order(3)
	public void updateExistingConsolidation() {
		logger.info("================ STEP 2 insert new expense in existing consolidation ================");
		Consolidation consolidation2 = loadConsolidation(CONSOLIDATION.getId());
		
		Expense expense = expenseService.save(newExpense("200"));
		final TransactionLine line = expense.getTransactions().get(0);
		consolidation2.addTransaction(line);
		
		final String trDescription = "test expense";
		line.setDescription(trDescription);
		
		Consolidation consolidation3 = saveConsolidation(consolidation2);

      Assert.assertEquals(consolidation2,consolidation3);
      Assert.assertNotSame(consolidation2, consolidation3);

		List<TransactionLine> lines = consolidation3.getTransactions();
		Assert.assertEquals(2, lines.size());
		
		checkNumberOfTransaction(CONSOLIDATION.getUid(), 2);
		
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				TransactionLine tr = persistenceHelper.getByUid(TransactionLine.class, line.getUid());
				Assert.assertEquals("Transaction line was not updated", trDescription, tr.getDescription());

			}
		});
		
		Assert.assertEquals(4, persistenceHelper.count(TransactionLine.class));
	}

	@Test
	@Order(4)
	public void removeExistingConsolidationTransactionLine() {
		logger.info("================ STEP 2.2 update expense in existing consolidation ================");
		Consolidation consolidation2 = loadConsolidation(CONSOLIDATION.getId());
		
		List<TransactionLine> lines = consolidation2.getTransactions();		
		Assert.assertEquals("number of transcation lines", 2, lines.size());
		
		TransactionLine line = lines.get(0);
		consolidation2.removeTransaction(line);
				
		Assert.assertNull(line.getConsolidation());
		Assert.assertFalse(consolidation2.getTransactions().contains(line));
		
		Consolidation consolidation3 = saveConsolidation(consolidation2);

      Assert.assertEquals(consolidation2,consolidation3);
      Assert.assertNotSame(consolidation2, consolidation3);

		List<TransactionLine> lines2 = consolidation3.getTransactions();
		Assert.assertEquals(1, lines2.size());
		
		checkNumberOfTransaction(CONSOLIDATION.getUid(), 1);
		
		TransactionLine line2 = persistenceHelper.getByUid(TransactionLine.class, line.getUid());
		Assert.assertNotNull("No transaction line", line2);
		Assert.assertNull("transaction line has a consolidation", line2.getConsolidation());
		
		Assert.assertEquals(4, persistenceHelper.count(TransactionLine.class));
	}


	//
	// ========================================================================
	//

	Consolidation saveConsolidation(final Consolidation p) {
		return new TransactionTemplate(transactionManager).execute(new TransactionCallback<Consolidation>() {
			@Override
			public Consolidation doInTransaction(TransactionStatus status) {
				Consolidation conso = consolidationService.save(p);
				conso.getTransactions().size();
				return conso;
			}
		});
	}

	Consolidation loadConsolidation(final Long pid) {
		return new TransactionTemplate(transactionManager).execute(new TransactionCallback<Consolidation>() {
			@Override
			public Consolidation doInTransaction(TransactionStatus status) {
				Consolidation conso = consolidationService.load(pid);
				conso.getTransactions().size();
				return conso;
			}
		});
	}

	void checkNumberOfTransaction(final String uid, final int expectedNumber) {
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Consolidation conso = persistenceHelper.getByUid(Consolidation.class, uid);
				Assert.assertEquals("Wrong number of transaction lines", expectedNumber, conso.getTransactions().size());
			}
		});
	}
}
