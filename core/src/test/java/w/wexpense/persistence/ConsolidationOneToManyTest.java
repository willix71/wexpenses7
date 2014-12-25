package w.wexpense.persistence;

import java.util.ArrayList;
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
	@Qualifier("consolidationService")
	private StorableService<Consolidation, Long> consolidationService;

	private static Long consolidationId;

	@Test
	@Order(1)
	public void setup() {
		Assert.assertNotNull(consolidationService);
	}

	@Test
	@Order(2)
	public void insertNewConsolidation() {
		logger.info("================ STEP 1 insert new consolidation and new expense ================");
		Consolidation consolidation = new Consolidation();
		consolidation.setInstitution(testPayee());
		consolidation.setDate(new Date());

		Expense expense1 = expenseService.save(newExpense("100"));	
		allocate(consolidation, expense1.getTransactions().get(0));
		
		Consolidation consolidation2 = saveConsolidation(consolidation);

		Assert.assertNotNull("Id has not been set", consolidation.getId());
		consolidationId = consolidation.getId();
		
		Assert.assertTrue(consolidation.equals(consolidation2));
		// instance is the same after insert
		Assert.assertTrue(consolidation == consolidation2);
		
		Consolidation consolidation3 = saveConsolidation(consolidation2);
		
		Assert.assertTrue(consolidation.equals(consolidation3));
		Assert.assertTrue(consolidation != consolidation3);

		List<TransactionLine> lines = consolidation3.getTransactions();
		Assert.assertEquals(1, lines.size());
		
		checkNumberOfTransaction(consolidation.getUid(), 1);
	}

	@Test
	@Order(3)
	public void updateExistingConsolidation() {
		logger.info("================ STEP 2 insert new expense in existing consolidation ================");
		Consolidation consolidation = loadConsolidation(consolidationId);
		
		Expense expense2 = expenseService.save(newExpense("200"));
		allocate(consolidation, expense2.getTransactions().get(0));
		final String trDescription = "test expense";
		expense2.getTransactions().get(0).setDescription(trDescription);
		final String trUid = expense2.getTransactions().get(0).getUid();
		
		Consolidation consolidation2 = saveConsolidation(consolidation);

		Assert.assertTrue(consolidation.equals(consolidation2));
		Assert.assertTrue(consolidation != consolidation2);

		List<TransactionLine> lines = consolidation2.getTransactions();
		Assert.assertEquals(2, lines.size());
		
		checkNumberOfTransaction(consolidation.getUid(), 2);
		
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
	public void removeExistingConsolidationTransactionLine() {
		logger.info("================ STEP 2.2 update expense in existing consolidation ================");
		Consolidation consolidation = loadConsolidation(consolidationId);
		List<TransactionLine> lines = consolidation.getTransactions();
		
		Assert.assertEquals("number of transcation lines", 2, lines.size());
		TransactionLine line = lines.get(0);
		deallocate(consolidation, line);
		final String trUid = line.getUid();
		
		Consolidation consolidation2 = saveConsolidation(consolidation);
		Assert.assertTrue(consolidation.equals(consolidation2));
		Assert.assertTrue(consolidation != consolidation2);

		List<TransactionLine> lines2 = consolidation2.getTransactions();
		Assert.assertEquals(1, lines2.size());
		
		checkNumberOfTransaction(consolidation.getUid(), 1);
		
		TransactionLine line2 = getByUid(TransactionLine.class, trUid);
		Assert.assertNotNull("No transaction line", line2);
		Assert.assertNull("transaction line has a consolidation", line2.getConsolidation());
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
				Consolidation conso = getByUid(Consolidation.class, uid);
				Assert.assertEquals("Wrong number of transaction lines", expectedNumber, conso.getTransactions().size());

			}
		});
	}

	Consolidation allocate(Consolidation conso, TransactionLine ... lines) {
		List<TransactionLine> allocations = conso.getTransactions();
		if (allocations == null) {
			allocations = new ArrayList<TransactionLine>();
			conso.setTransactions(allocations);
		}
		for (TransactionLine tl : lines) {
			allocations.add(tl);
			tl.setConsolidation(conso);
		}
		return conso;
	}

	Consolidation deallocate(Consolidation conso, TransactionLine ... lines) {
		List<TransactionLine> allocations = conso.getTransactions();
		for (TransactionLine tl : lines) {
			allocations.remove(tl);
			tl.setConsolidation(null);
		}
		return conso;
	}
}
