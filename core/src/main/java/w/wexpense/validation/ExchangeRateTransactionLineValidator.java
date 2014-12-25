package w.wexpense.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import w.wexpense.model.Account;
import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;

public class ExchangeRateTransactionLineValidator implements ConstraintValidator<ExchangeRateTransactionLineized, TransactionLine> {

	@Override
	public void initialize(ExchangeRateTransactionLineized constraintAnnotation) {
		// void
	}

	@Override
	public boolean isValid(TransactionLine transactionLine, ConstraintValidatorContext context) {
		Expense x = transactionLine.getExpense();
		Currency fromC = (x == null)?null:x.getCurrency();
		Account acc = transactionLine.getAccount();
		Currency toC = (acc == null)?null:acc.getCurrency();		
		if (fromC == null || toC == null) {
			return true; // no validation
		}
		ExchangeRate r = transactionLine.getExchangeRate();
		if (fromC.equals(toC)) {
			return r == null; // same currency so no exchange rate
		}
		
		return r!=null && fromC.equals(r.getFromCurrency()) && toC.equals(r.getToCurrency());
	}
}