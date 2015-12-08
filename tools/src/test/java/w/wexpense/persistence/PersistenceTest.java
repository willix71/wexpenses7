package w.wexpense.persistence;

import static w.wexpense.model.enums.TransactionLineEnum.OUT;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.test.config.AbstractTest;
import w.wexpense.test.config.DatabasePopulationConfig;
import w.wexpense.test.utils.PersistenceHelper;
import w.wexpense.utils.ExpenseUtils;

@Configuration
class PersistenceTestConfig extends DatabasePopulationConfig {
	public PersistenceTestConfig() {
		Currency chf = add(new Currency("CHF", "Swiss Francs", 20));
		Currency euro = add(new Currency("EUR", "Euro", 100));
		Currency usd = add(new Currency("USD", "US Dollar", 100));
		add(new Currency("GBP", "British Pounds", 100));

		Country ch = add(new Country("CH", "Switzerland", chf));
		Country f = add(new Country("FR", "France", euro));
		add(new Country("IT", "Italie", euro));
		add(new Country("DE", "Germany", euro));
		add(new Country("US", "United States of America", usd));

		add(new City(null, "Paris", f));
		add(new City("1260", "Nyon", ch));
		add(new City("1197", "Prangins", ch));
	};
}

@ContextConfiguration(classes = { PersistenceTestConfig.class })
public class PersistenceTest extends AbstractTest {

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private PersistenceHelper persistenceHelper;

	@Test
	@Order(1)
	public void setup() {
		Assert.assertNotNull(persistenceHelper);
	}

	@Test
	@Order(2)
	@Transactional
	public void testSetup() {
		Assert.assertEquals(4, persistenceHelper.count(Currency.class));
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

		final City city = new City("123", "somewhere", ch);

		final Payee payee = new Payee();
		payee.setName("Test");
		payee.setCity(city);

		// create an expense
		final Expense expense1 = ExpenseUtils.newExpense(new Date(), amount, payee, ch.getCurrency(), cashAccount,
				cashAccount);

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
