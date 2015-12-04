package w.wexpense.dta;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;

public class DtaCommonTestData {

	public static final Currency chf = new Currency("CHF", "Swiss Francs", 20);		
	public static final Country ch = new Country("CH", "Switzerland", chf);
	public static final City nyon = new City("1260", "Nyon", ch);
	public static final City gland = new City("1196","Gland",ch);
	public static final City bursins = new City("1183","Bursins",ch);
	public static final City prangins = new City("1197", "Prangins",ch);
	
	public static final ExpenseType bvo = new ExpenseType("bvo", true, BvoDtaFormater.class.getName());
	public static final ExpenseType bvr = new ExpenseType("bvr", true, BvrDtaFormater.class.getName());
	public static final ExpenseType iban = new ExpenseType("iban", true, IbanDtaFormater.class.getName());
	
	public static final Payee williamKeyser;
	static {
		williamKeyser = new Payee();
		williamKeyser.setName("William Keyser");
		williamKeyser.setAddress1("11 ch du Grand Noyer");
		williamKeyser.setCity(prangins);
		williamKeyser.setIban("CH650022822851333340B");
	}
	
	public static Payment createPaymentData(int day, int month, int year, String filename, Expense ...expenses) {
		// === Payment ===
		Payment payment = new Payment();
		payment.setFilename(filename);
		payment.setDate(createDate(day, month, year));
		
		for(Expense expense:expenses) {
			expense.setPayment(payment);
		}
		payment.setExpenses(Arrays.asList(expenses));

		return payment;
	}

	public static Date createDate(int day, int month, int year) {
		return new GregorianCalendar(year,month-1,day).getTime();
	}
}
