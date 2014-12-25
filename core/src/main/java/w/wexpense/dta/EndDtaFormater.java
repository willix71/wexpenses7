package w.wexpense.dta;

import static w.wexpense.dta.DtaHelper.pad;

import java.text.DecimalFormat;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;

public class EndDtaFormater {

	public static final String TRANSACTION_TYPE = "890";	
	
	public String format(Payment payment, int index) {
		StringBuilder line01 = new StringBuilder();
		line01.append("01");

		line01.append(DtaHelper.getHeader(TRANSACTION_TYPE, payment.getDate(), index, null, false));
		
		double d = 0;
		for(Expense expense: payment.getExpenses()) {
			d += expense.getAmount().doubleValue();
		}
		String amount = new DecimalFormat("0.00").format(d).replace(".", ",");
		line01.append(pad(amount,16));
		
		line01.append(pad(59));
		
		return line01.toString();
	}
}
