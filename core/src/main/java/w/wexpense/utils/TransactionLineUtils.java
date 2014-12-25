package w.wexpense.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import w.wexpense.model.Account;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class TransactionLineUtils {

	public static void updateAmount(Collection<TransactionLine> transactions, BigDecimal newAmount, BigDecimal oldAmount) {
		for(TransactionLine transaction: transactions) {
			BigDecimal amnt = transaction.getAmount();
			if (amnt==null || (oldAmount!=null && oldAmount.compareTo(amnt)==0) ) {
				transaction.setAmount(newAmount);
			}
			transaction.updateValue();
		}
	}

	public static TransactionLine newTransactionLine(Expense expense, Collection<TransactionLine> transactions) {		
		BigDecimal amount = expense.getAmount();
		if (amount==null) {
			amount = BigDecimal.ZERO;
		}
		TransactionLineEnum factor;
		BigDecimal value;
		
		BigDecimal[] deltaAndTotalOut = getAmountDeltaAndTotals(transactions);
		if (deltaAndTotalOut[1].compareTo(amount) < 0) {
			value = amount.subtract(deltaAndTotalOut[1]);
			factor = TransactionLineEnum.OUT;
		} else if (deltaAndTotalOut[2].compareTo(amount) < 0) { 
			value = amount.subtract(deltaAndTotalOut[2]);
			factor = TransactionLineEnum.IN;
		} else {
			factor = deltaAndTotalOut[0].compareTo(BigDecimal.ZERO)<0?TransactionLineEnum.IN:TransactionLineEnum.OUT;
			value = deltaAndTotalOut[0].abs();
		}
		TransactionLine line = new TransactionLine();
		line.setExpense(expense);
		line.setDate(expense.getDate());
		line.setFactor(factor);
		line.setAmount(value);
		line.setValue(value);
		return line;
	}
	
	public static BigDecimal[] getAmountDeltaAndTotals(Collection<TransactionLine> transactions) {
		return getDeltaAndTotals(true, transactions);
	}
	
	public static BigDecimal[] getValueDeltaAndTotals(Collection<TransactionLine> transactions) {
		return getDeltaAndTotals(false, transactions);
	}
	
	private static BigDecimal[] getDeltaAndTotals(boolean forAmount, Collection<TransactionLine> transactions) {
		BigDecimal[] total = { new BigDecimal(0), new BigDecimal(0), new BigDecimal(0)};
		for(TransactionLine transaction: transactions) {
			BigDecimal amount = forAmount?transaction.getAmount():transaction.getValue();
			TransactionLineEnum factor = transaction.getFactor();
			if (amount != null && factor != null) {
				if (TransactionLineEnum.OUT==factor) {					
					total[0] = total[0].subtract(amount);
					total[1] = total[1].add(amount);
				} else if (TransactionLineEnum.IN==factor) {
					total[0] = total[0].add( amount );
					total[2] = total[2].add(amount);
				}
			}
		}
		return total;
	}
	
	public static void sortAndBalance(List<TransactionLine> lines) {
		balance(sortForBalance(lines));
	}
	
	public static List<TransactionLine> sortForBalance(List<TransactionLine> lines) {
		Collections.sort(lines, new Comparator<TransactionLine>() {
			public int compare(TransactionLine o1, TransactionLine o2) {
				int v = o1.getDate().compareTo(o2.getDate());
				if (v!=0) return v;
				if (o1.getFactor()==o2.getFactor()) return 0;
				if (o1.getFactor()==TransactionLineEnum.SUM) return -1;
				if (o2.getFactor()==TransactionLineEnum.SUM) return 1;
				if (o1.getFactor()==TransactionLineEnum.OUT) return -1;
				if (o2.getFactor()==TransactionLineEnum.OUT) return 1;
				if (o1.getFactor()==TransactionLineEnum.IN) return -1;
				if (o2.getFactor()==TransactionLineEnum.IN) return 1;
				return 0;
			}
		});
		return lines;
	}
	
	public static void balance(Iterable<TransactionLine> lines) {
		Map<Account, BigDecimal> balances = new HashMap<Account, BigDecimal>();
		for(TransactionLine line: lines) {
			if (line.getBalance() != null) continue;
			
			Account account = line.getAccount();
			BigDecimal balance = balances.get(account);
			if (line.getFactor() == TransactionLineEnum.IN) {
				balance = balance == null?line.getValue():balance.add(line.getValue());
			} else if (line.getFactor() == TransactionLineEnum.OUT) { 
				balance = balance == null?line.getValue().negate():balance.subtract(line.getValue());
			} else {
				balanceSum(balance == null?BigDecimal.ZERO:balance, line);

				// reset the balance
				balance = line.getValue();
			}
			line.setBalance(balance);
			balances.put(account, balance);
		}		
	}
	
	private static void balanceSum(BigDecimal balance, TransactionLine sumLine) {	
		Expense expense = sumLine.getExpense();
		
		TransactionLine deltaLine = null;
		TransactionLine counterLine = null;
		for(TransactionLine l: expense.getTransactions()) {
			if (l.getAccount().equals(sumLine.getAccount())) {
				if (l!=sumLine) {
					deltaLine = l;
				}
			} else {
				counterLine = l;
			}
		}

		if (sumLine.getValue().equals(balance)) {
			counterLine.setAmount(BigDecimal.ZERO);
			counterLine.setFactor(TransactionLineEnum.OUT);

			if (deltaLine != null) {
				expense.getTransactions().remove(deltaLine);
			}
		} else {					
			BigDecimal delta = sumLine.getValue().subtract(balance);
			TransactionLineEnum deltaFactor = delta.signum()>0?TransactionLineEnum.IN:TransactionLineEnum.OUT;
			TransactionLineEnum counterFactor = delta.signum()<0?TransactionLineEnum.IN:TransactionLineEnum.OUT;			
			delta = delta.abs();
			
			counterLine.setAmount(delta);			
			counterLine.setFactor(counterFactor);
			counterLine.updateValue();
			
			if (deltaLine == null) {
				deltaLine = new TransactionLine();
				deltaLine.setDate(sumLine.getDate());
				deltaLine.setPeriod(sumLine.getPeriod());
				deltaLine.setAccount(sumLine.getAccount());
				expense.addTransaction(deltaLine);
			}				
			deltaLine.setAmount(delta.abs());
			deltaLine.setFactor(deltaFactor);
			deltaLine.updateValue();
			deltaLine.setBalance(sumLine.getValue());
		}
		
		sumLine.setBalance(sumLine.getValue());
	}

	public static List<TransactionLine> getAllTransactionLines(Iterable<Expense> xs) {	
		List<TransactionLine> lines = new ArrayList<TransactionLine>();
		for(Expense x: xs ){ lines.addAll(x.getTransactions()); }
		return lines;
	}
}
