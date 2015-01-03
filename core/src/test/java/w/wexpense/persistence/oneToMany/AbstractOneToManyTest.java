package w.wexpense.persistence.oneToMany;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

import w.junit.extras.OrderedSpringJUnit4ClassRunner;
import w.wexpense.dta.BvoDtaFormater;
import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.persistence.oneToMany.AbstractOneToManyTest.OneToManyConfiguror;
import w.wexpense.test.utils.PersistenceHelper;
import w.wexpense.test.utils.TestDatabaseConfiguror;
import w.wexpense.test.utils.TestDatabasePopulator;
import w.wexpense.test.utils.TestServiceConfiguror;
import w.wexpense.utils.ExpenseUtils;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
@ContextConfiguration(classes = { TestDatabaseConfiguror.class, OneToManyConfiguror.class, TestServiceConfiguror.class })
public abstract class AbstractOneToManyTest {

   public static final Logger logger = LoggerFactory.getLogger(PaymentOneToManyTest.class);

   @Configuration
   public static class OneToManyConfiguror  extends TestDatabasePopulator {
      @Override
      public List<Object> getPopulation() {
         List<Object> entities = new ArrayList<Object>();
         
         Currency chf = new Currency("CHF", "Swiss Francs", 100);
         entities.add(chf);
         Country ch = new Country("CH", "Swiss", chf);
         entities.add(ch);
         City c = new City("1010", "testCity", ch);
         entities.add(c);
         
         ExpenseType bvo = new ExpenseType("BVO", true, BvoDtaFormater.class.getName());
         entities.add(bvo);
         
         Payee payee = new Payee();
         payee.setName("testPayee");
         payee.setPostalAccount("1-11111-1");
         payee.setCity(c);
         entities.add(payee);
         
         Payee wk = new Payee();
         wk.setName("wk");
         wk.setPostalAccount("1-123-1");
         wk.setCity(c);
         wk.setIban("CH120022822877005740J");
         entities.add(wk);
         
         Account inAccount = new Account();
         inAccount.setName("in");
         inAccount.setExternalReference("123123123123123");
         entities.add(inAccount);
         
         Account outAccount = new Account();
         outAccount.setName("out");
         outAccount.setExternalReference("321321321321321");
         outAccount.setOwner(wk);
         entities.add(outAccount);
         
         return entities;
      };
   }
   
   @PersistenceContext
   protected EntityManager entityManager;

   @Autowired
   protected PlatformTransactionManager transactionManager;

   protected PersistenceHelper persistenceHelper;

   @PostConstruct
   public void postConstuct() {
      persistenceHelper = new PersistenceHelper(entityManager, transactionManager);            
   }
   
   protected Country CH() { return persistenceHelper.get(Country.class, "CH"); }

   protected Currency CHF() { return persistenceHelper.get(Currency.class, "CHF"); }

   protected City testCity() { return persistenceHelper.getByName(City.class, "testCity"); }

   protected Payee testPayee() { return persistenceHelper.getByName(Payee.class, "testPayee"); }

   protected ExpenseType BVO() { return persistenceHelper.getByName(ExpenseType.class, "BVO"); }

   protected Account inAccount() { return persistenceHelper.getByName(Account.class, "in"); }

   protected Account outAccount() { return persistenceHelper.getByName(Account.class, "out"); }

   protected Expense newExpense(String amount) {
      Expense x = ExpenseUtils.newExpense(new Date(), new BigDecimal(amount), CHF(), testPayee(), BVO(), "1 12345 12345 12345 12345", outAccount(), inAccount());
      return x;
   }

   protected Expense newlines(Expense x) {
      ExpenseUtils.newTransactionLine(x, outAccount(), TransactionLineEnum.OUT);
      ExpenseUtils.newTransactionLine(x, inAccount(), TransactionLineEnum.IN);      
      return x;
   }
   
   @Test
   @Order(-1)
   public void testDB() {
      Assert.assertEquals(1,persistenceHelper.count(Currency.class));
      Assert.assertEquals(1,persistenceHelper.count(Country.class));
      Assert.assertEquals(0,persistenceHelper.count(Expense.class));
      Assert.assertEquals(0,persistenceHelper.count(TransactionLine.class));
   }
}
