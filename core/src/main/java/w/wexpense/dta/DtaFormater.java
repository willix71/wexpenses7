package w.wexpense.dta;

import java.util.List;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;

import com.google.common.collect.Multimap;

public interface DtaFormater {

	Multimap<String, String> validate(Expense expense);

	String payee(Expense expense);
	
	List<String> format(Payment payment, int index, Expense expense) throws DtaException;
}
