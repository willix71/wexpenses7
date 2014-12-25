package w.wexpense.persistence;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import w.junit.extras.OrderedSpringJUnit4ClassRunner;
import w.wexpense.dta.BvoDtaFormater;
import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.enums.AccountEnum;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.service.StorableService;
import w.wexpense.test.utils.TestContextConfiguror;
import w.wexpense.utils.ExpenseUtils;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
@ContextConfiguration(classes=TestContextConfiguror.class)
//@ContextConfiguration(locations = { "classpath:persistence-test-context.xml", "classpath:persistence-service-test-context.xml" })
public abstract class AbstractOneToManyTest {

	public static final Logger logger = LoggerFactory.getLogger(PaymentOneToManyTest.class);

	@Autowired
	protected PlatformTransactionManager transactionManager;

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	@Qualifier("expenseService")
	protected StorableService<Expense, Long> expenseService;

	private static Country CH;
	protected Country CH() {
		if (CH == null) {
			persist(new Country("CH", "Swiss", CHF()));
			CH = entityManager.find(Country.class, "CH");
		}
		return CH;
	}
	private static Currency CHF;
	protected Currency CHF() {
		if (CHF == null) {
			persist(new Currency("CHF", "Swiss Francs", 100));
			CHF = entityManager.find(Currency.class, "CHF");
		}
		return CHF;
	}
	private static City testCity;
	protected City testCity() {
		if (testCity == null) {
			City c = new City("0000","testCity", CH());
			persist(c);
		
			testCity = getByUid(City.class, c.getUid());
		}
		return testCity;
	}
	private static Payee testPayee;
	protected Payee testPayee() {
		if (testPayee == null) {			
			Payee payee = new Payee();
			payee.setName("TestPayee");
			payee.setPostalAccount("1-11111-1");			
			payee.setCity(testCity());
			persist(payee);

			testPayee = getByUid(Payee.class, payee.getUid());		}
		return testPayee;
	}
	private static ExpenseType BVO;
	protected ExpenseType BVO() {
		if (BVO == null) {
			ExpenseType type = new ExpenseType("BVO", true, BvoDtaFormater.class.getName());
			persist(type);
			
			BVO = getByUid(ExpenseType.class, type.getUid());
		}
		return BVO;
	}
	private static Account inAccount;
	protected Account inAccount() {
		if (inAccount == null) {
			Account acc = new Account();
			acc.setName("in");
			acc.setExternalReference("123123123123123");
			acc.setType(AccountEnum.ASSET);
			persist(acc);
			
			inAccount = getByUid(Account.class, acc.getUid());
		}
		return inAccount;
	}
	private static Account outAccount;
	protected Account outAccount() {
		if (outAccount == null) {
			Account acc = new Account();
			acc.setName("out");
			acc.setExternalReference("321321321321321");
			acc.setType(AccountEnum.EXPENSE);
			persist(acc);
			
			outAccount = getByUid(Account.class, acc.getUid());
		}
		return outAccount;
	}


	protected Expense newExpense(String amount) {
		Expense x = ExpenseUtils.newExpense(new Date(), new BigDecimal(amount), CHF(), testPayee(), BVO(), "1 12345 12345 12345 12345");
		newlines(x);		
		return x;
	}
	
	protected Expense newlines(Expense x) {
		ExpenseUtils.addTransactionLine(x, inAccount(), TransactionLineEnum.IN);
		ExpenseUtils.addTransactionLine(x, outAccount(), TransactionLineEnum.OUT);
		return x;
	}
	
	protected void persist(final Object... objs) {
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				for (Object o : objs) {
					entityManager.persist(o);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected <T> T getByUid(Class<T> clazz, String uid) {
		Query q = entityManager.createQuery("from " + clazz.getSimpleName() + " x where x.uid=:uid");
		return (T) q.setParameter("uid", uid).getSingleResult();
	}
	

	Expense loadExpenseByUid(final Long pid) {
		return new TransactionTemplate(transactionManager).execute(new TransactionCallback<Expense>() {
			@Override
			public Expense doInTransaction(TransactionStatus status) {
				Expense x = expenseService.load(pid);
				x.getTransactions().size();
				return x;
			}
		});
	}
	
}
