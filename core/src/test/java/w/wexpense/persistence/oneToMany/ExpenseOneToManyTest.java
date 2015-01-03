package w.wexpense.persistence.oneToMany;

import java.math.BigDecimal;
import java.util.Date;
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

@ActiveProfiles("ExpenseOneToManyTest")
public class ExpenseOneToManyTest extends AbstractOneToManyTest {

   @Autowired
   @Qualifier("expenseService")
   private StorableService<Expense, Long> expenseService;

   private static final Expense EXPENSE = new Expense();

   @Test
   @Order(1)
   public void setup() {
      Assert.assertNotNull(expenseService);
   }

   @Test
   @Order(2)
   public void insertNewExpense() {
      logger.info("================ STEP 1 insert new expense and new expense ================");

      ExpenseUtils.fillExpense(EXPENSE, new Date(), testPayee(), new BigDecimal("100"), CHF(), BVO(), outAccount(), inAccount());

      Expense expense2 = saveExpense(EXPENSE);

      Assert.assertNotNull("Id has not been set", EXPENSE.getId());

      Assert.assertEquals(EXPENSE, expense2);
      Assert.assertSame(EXPENSE, expense2);

      // modify something
      final String externalRef = "10 00000 12345 56789";
      expense2.setExternalReference(externalRef);

      Expense expense3 = saveExpense(expense2);

      Assert.assertEquals(EXPENSE, expense3);
      Assert.assertNotSame(EXPENSE, expense3);

      Assert.assertEquals(externalRef, expense3.getExternalReference());

      List<TransactionLine> lines = expense3.getTransactions();
      Assert.assertEquals(2, lines.size());

      checkNumberOfTransaction(EXPENSE.getUid(), 2);
      
      Assert.assertEquals(2, persistenceHelper.count(TransactionLine.class));
   }

   @Test
   @Order(3)
   public void updateExistingExpense() {
      List<TransactionLine> lines;

      logger.info("================ STEP 2 modify transaction line in existing expense ================");
      Expense expense2 = loadExpense(EXPENSE.getId());

      lines = expense2.getTransactions();
      Assert.assertEquals(2, lines.size());

      final String trDescription = "test expense";
      final TransactionLine tl = expense2.getTransactions().get(0);
      tl.setDescription(trDescription);

      Expense expense3 = saveExpense(expense2);

      Assert.assertEquals(expense2, expense3);
      Assert.assertNotSame(expense2, expense3);

      lines = expense3.getTransactions();
      Assert.assertEquals(2, lines.size());

      checkNumberOfTransaction(EXPENSE.getUid(), 2);

      new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
         @Override
         protected void doInTransactionWithoutResult(TransactionStatus status) {
            TransactionLine tr = persistenceHelper.getByUid(TransactionLine.class, tl.getUid());
            Assert.assertEquals("Transaction line was not updated", trDescription, tr.getDescription());

         }
      });
      
      Assert.assertEquals(2, persistenceHelper.count(TransactionLine.class));
   }

   @Test
   @Order(4)
   public void removeExistingExpenseTransactionLine() {
      List<TransactionLine> lines;

      logger.info("================ STEP 3 remove transaction lines from expense ================");
      Expense expense2 = loadExpense(EXPENSE.getId());

      lines = expense2.getTransactions();
      Assert.assertEquals(2, lines.size());

      final TransactionLine line = lines.get(0);
      expense2.removeTransaction(line);

      BigDecimal half = line.getAmount().divide(BigDecimal.valueOf(2));
      ExpenseUtils.newTransactionLine(expense2, line.getAccount(), line.getFactor(), half);
      ExpenseUtils.newTransactionLine(expense2, line.getAccount(), line.getFactor(), half);

      Expense expense3 = saveExpense(expense2);
      
      Assert.assertEquals(expense2, expense3);
      Assert.assertNotSame(expense2, expense3);

      lines = expense3.getTransactions();
      Assert.assertEquals(3, lines.size());

      checkNumberOfTransaction(expense2.getUid(), 3);

      try {
         persistenceHelper.getByUid(TransactionLine.class, line.getUid());
         Assert.fail("Transaction line should not exist!");
      } catch (NoResultException nre) {
         // YES !!!
      }
      
      Assert.assertEquals(3, persistenceHelper.count(TransactionLine.class));
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
            Expense x = persistenceHelper.getByUid(Expense.class, uid);
            Assert.assertEquals("Wrong number of transaction lines", expectedNumber, x.getTransactions().size());

         }
      });
   }

}
