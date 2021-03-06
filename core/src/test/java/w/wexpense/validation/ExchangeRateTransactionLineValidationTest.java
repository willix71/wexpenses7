package w.wexpense.validation;

import java.math.BigDecimal;

import org.junit.Test;

import w.utils.DateUtils;
import w.wexpense.model.Account;
import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.AccountEnum;
import w.wexpense.utils.ExpenseUtils;

public class ExchangeRateTransactionLineValidationTest extends AbstractValidationTest {

	Currency CHF = new Currency("CHF", "Switzerland",20);
	Currency GBP = new Currency("GBP", "Pounds",100);
	Currency USD = new Currency("USD", "US",100);
	

	public Expense getExpense() {
	   return ExpenseUtils.newExpense(DateUtils.toDate(), new BigDecimal("10"), CHF, new Payee());
	}
	
	@Test
	public void testSimpleGood() {
		TransactionLine l = ExpenseUtils.newTransactionLine(getExpense(), new Account());
		good("simplest transaction line", l);
		
		l.setAccount(new Account(null,1,"1",AccountEnum.EXPENSE, CHF));
		good("same currency", l);
		
		l.setAccount(new Account(null,1,"1",AccountEnum.EXPENSE, USD));
		l.setExchangeRate(new ExchangeRate(CHF, USD, 1.4));
		good("matching currency", l);
	}
	
	@Test
	public void testSimpleBad() {
		TransactionLine l = ExpenseUtils.newTransactionLine(getExpense(), new Account(null,1,"1",AccountEnum.EXPENSE, USD));
		bad("different currency", l);
		
		l.setExchangeRate(new ExchangeRate(CHF, GBP, 1.4));
		bad("not matching currency", l);
	}
	
}