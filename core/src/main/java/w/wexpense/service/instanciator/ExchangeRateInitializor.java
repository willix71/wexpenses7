package w.wexpense.service.instanciator;

import java.util.Arrays;

import w.wexpense.model.Account;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;

public class ExchangeRateInitializor implements Initializor<ExchangeRate> {

	@Override
   public Object[] initialize(ExchangeRate xRate, Object[] args) {
   	if (args==null || args.length == 0) return args;
   	if (! (args[0] instanceof TransactionLine)) return args;
   	
   	TransactionLine tl= (TransactionLine) args[0];
   	Expense x = tl.getExpense();
   	if (x != null) {
   		xRate.setDate(x.getDate());
   		xRate.setInstitution(x.getPayee());
   		xRate.setFromCurrency(x.getCurrency());
   	}
   	Account acc = tl.getAccount();
   	if (acc != null) {
   		xRate.setToCurrency(acc.getCurrency());
   	}
   	
   	return Arrays.copyOfRange(args, 1, args.length);
   }
}
