package w.wexpense.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import w.utils.DateUtils;
import w.wexpense.model.enums.AccountEnum;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.utils.ExpenseUtils;
import w.wexpense.utils.TransactionLineUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author willy
 *
 */
public class TransactionLineUtilsTest {

	public static Currency CHF = new Currency("CHF","Swiss Francs",20);
	public static Currency USD = new Currency("USD","US Dollars",100);
	public static Account A1 = new Account(null, 1, "A1", AccountEnum.EXPENSE, CHF);
	public static Account A11 = new Account(A1, 11, "A11", AccountEnum.EXPENSE, CHF);
	public static Account A111 = new Account(A11, 111, "A111", AccountEnum.EXPENSE, CHF);
	public static Account A12 = new Account(A1, 12, "A12", AccountEnum.EXPENSE, CHF);
	public static Account A13 = new Account(A1, 13, "A13", AccountEnum.EXPENSE, CHF);
	public static Account A2 = new Account(null, 2, "A2", AccountEnum.ASSET, USD);
	public static Account App = new Account(null, 9, "App", AccountEnum.EXPENSE, CHF);
	
	
	/**
	 * @return the initial expenses to test the balance on
	 */
	private List<Expense> initExpenses() {
		List<Expense> xs = new ArrayList<Expense>();
		xs.add( ExpenseUtils.newExpense(DateUtils.getDate(1,2,2013), new BigDecimal("100"), CHF, A11, A111) );
		xs.add( ExpenseUtils.newExpense(DateUtils.getDate(1,4,2014), new BigDecimal("45"), CHF, A11, A12) );
		xs.add( ExpenseUtils.newExpense(DateUtils.getDate(1,1,2012), new BigDecimal("200"), CHF, A13, A11) );	
		
		Expense xsum = ExpenseUtils.newExpense(DateUtils.getDate(1,10,2015), new BigDecimal("0"), CHF);
		ExpenseUtils.newTransactionLine(xsum, App, TransactionLineEnum.IN);
		ExpenseUtils.newTransactionLine(xsum, A12, TransactionLineEnum.SUM, new BigDecimal("300"));
		xs.add(xsum);
		
		xs.add( ExpenseUtils.newExpense(DateUtils.getDate(1,1,2016), new BigDecimal("88"), CHF, A11, A12) );
		return xs;
	}

	
	@Test
	public void testBalanceSort() {
		List<TransactionLine> initlines = TransactionLineUtils.getAllTransactionLines(initExpenses());
		
		List<TransactionLine> lines = TransactionLineUtils.sortForBalance(initlines);
		Date d = DateUtils.getDate(1,1,2000);
		
		for(TransactionLine l: lines) {
			Assert.assertFalse(l.getDate().before(d));
			if (l.getDate().equals(d)) {
				// the first line is either a SUM or an OUT so the second has to be an IN
				Assert.assertTrue(TransactionLineEnum.IN.equals(l.getFactor()));
			}
			d = l.getDate();
		}
	}

	@Test
	public void testBalance() {	
		List<Expense> xs = initExpenses();
		
		Multimap<Account, BigDecimal> balances = ArrayListMultimap.create();
		balances.putAll(A11, Arrays.asList(new BigDecimal[]{new BigDecimal("200"),new BigDecimal("100"),new BigDecimal("55"),new BigDecimal("-33")}));
		balances.put(A111, new BigDecimal("100"));
		balances.putAll(A12, Arrays.asList(new BigDecimal[]{new BigDecimal("45"),new BigDecimal("300"),new BigDecimal("300"),new BigDecimal("388")}));
		balances.put(A13, new BigDecimal("-200"));
		balances.put(App, new BigDecimal("-255"));
		
		TransactionLineUtils.sortAndBalance(TransactionLineUtils.getAllTransactionLines(xs));

		checkBalance(balances, xs);
	}
	
	@Test
	public void testReBalance() {	
		List<Expense> xs = initExpenses();
		
		Multimap<Account, BigDecimal> balances = ArrayListMultimap.create();
		balances.putAll(A11, Arrays.asList(new BigDecimal[]{new BigDecimal("200"),new BigDecimal("100"),new BigDecimal("55"),new BigDecimal("-33")}));
		balances.put(A111, new BigDecimal("100"));
		balances.putAll(A12, Arrays.asList(new BigDecimal[]{new BigDecimal("45"),new BigDecimal("300"),new BigDecimal("300"),new BigDecimal("388")}));
		balances.put(A13, new BigDecimal("-200"));
		balances.put(App, new BigDecimal("-255"));
		
		TransactionLineUtils.sortAndBalance(TransactionLineUtils.getAllTransactionLines(xs));

		clearBalance(TransactionLineUtils.getAllTransactionLines(xs));
		
		TransactionLineUtils.sortAndBalance(TransactionLineUtils.getAllTransactionLines(xs));

		checkBalance(balances, xs);
	}
	
	@Test
	public void testReBalanceModified() {	
		List<Expense> xs = initExpenses();
		
		Multimap<Account, BigDecimal> balances = ArrayListMultimap.create();
		balances.putAll(A11, Arrays.asList(new BigDecimal[]{new BigDecimal("200"),new BigDecimal("100"),new BigDecimal("55"),new BigDecimal("43"),new BigDecimal("-45")}));
		balances.put(A111, new BigDecimal("100"));
		balances.putAll(A12, Arrays.asList(new BigDecimal[]{new BigDecimal("45"),new BigDecimal("57"),new BigDecimal("300"),new BigDecimal("300"),new BigDecimal("388")}));
		balances.put(A13, new BigDecimal("-200"));
		balances.put(App, new BigDecimal("-243"));
		
		TransactionLineUtils.sortAndBalance(TransactionLineUtils.getAllTransactionLines(xs));

		xs.add(ExpenseUtils.newExpense(DateUtils.getDate(1,4,2014), new BigDecimal("12"), CHF, A11, A12));
		
		clearBalance(TransactionLineUtils.getAllTransactionLines(xs));
		
		TransactionLineUtils.sortAndBalance(TransactionLineUtils.getAllTransactionLines(xs));

		checkBalance(balances, xs);
	}
	
	// =================================
	
	public void checkBalance(Multimap<Account, BigDecimal> balances, List<Expense> xs) {
		// sort the transaction because they might have been some new lines added in the balance process
		for(TransactionLine l : TransactionLineUtils.sortForBalance( TransactionLineUtils.getAllTransactionLines(xs) )) {
			List<BigDecimal> values = (List<BigDecimal>) balances.get(l.getAccount());
			if (!values.isEmpty()) {
				BigDecimal v = values.remove(0);
				BigDecimal b = l.getBalance();
				// can't use equal because the scale has to be the same
				Assert.assertTrue(v + " is different than " + b, v.compareTo(b) == 0);
			}
		}
	}
	
	public void clearBalance(List<TransactionLine> lines) {
		for(TransactionLine l:lines) l.setBalance(null);
	}
}
