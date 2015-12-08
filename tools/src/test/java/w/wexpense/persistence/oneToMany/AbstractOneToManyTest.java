package w.wexpense.persistence.oneToMany;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

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
import w.wexpense.test.config.AbstractTest;
import w.wexpense.test.config.DatabasePopulationConfig;
import w.wexpense.test.utils.PersistenceHelper;
import w.wexpense.utils.AccountUtils;
import w.wexpense.utils.ExpenseUtils;
import w.wexpense.utils.PayeeUtils;

@Configuration
class OneToManyConfig  extends DatabasePopulationConfig {
   public OneToManyConfig() {
      Currency chf = add(new Currency("CHF", "Swiss Francs", 100));
      Country ch = add(new Country("CH", "Swiss", chf));
      City c = add(new City("1010", "testCity", ch));

      add(new ExpenseType("BVO", true, BvoDtaFormater.class.getName()));
      
      add(PayeeUtils.newPayee("testPayee", c, "1-11111-1"));
      
      add(AccountUtils.newAccount(1, "in", "123123123123123"));
      
      add(AccountUtils.newAccount(2, "out","321321321321321", add(PayeeUtils.newPayee("wk", c, "1-123-1", "CH120022822877005740J"))));
    };
}

@ContextConfiguration(classes = { OneToManyConfig.class })
public abstract class AbstractOneToManyTest extends AbstractTest {

   public static final Logger logger = LoggerFactory.getLogger(PaymentOneToManyTest.class);
  
   @PersistenceContext
   protected EntityManager entityManager;
   
   @Autowired
   protected PersistenceHelper persistenceHelper;
   
   @Autowired
   protected PlatformTransactionManager transactionManager;
   
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
