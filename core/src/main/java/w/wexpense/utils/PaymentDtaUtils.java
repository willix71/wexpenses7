package w.wexpense.utils;

import java.util.ArrayList;
import java.util.List;

import w.wexpense.dta.DtaException;
import w.wexpense.dta.DtaFormater;
import w.wexpense.dta.EndDtaFormater;
import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;

public class PaymentDtaUtils {

	public static List<PaymentDta> getPaymentDtas(Payment payment) throws DtaException {
		List<PaymentDta> dtas = new ArrayList<PaymentDta>();
		int index=0;
		int order=0;
		for(Expense expense:payment.getExpenses()) {
			DtaFormater formater = getDtaFormater(expense);
			for(String line: formater.format(payment, ++index, expense)) {
				dtas.add(new PaymentDta(payment, ++order, expense, line));
			}
		}
		
		String endLine = new EndDtaFormater().format(payment, ++index);
		dtas.add(new PaymentDta(payment, ++order, null, endLine));

		return dtas;
	}

	public static DtaFormater getDtaFormater(Expense expense) throws DtaException {
		try {	
			String formaterName = expense.getType().getPaymentGeneratorClassName();
			return (DtaFormater) Class.forName(formaterName).newInstance();
		}
		catch(Exception e) {	
			throw new DtaException("Can not generate DTA for type " + expense.getType(), e);
		}
	}
	
	public static String getDtaLines(List<PaymentDta> dtaLines, boolean singleLine) {
		StringBuilder sb = new StringBuilder();
		for(PaymentDta dta: dtaLines) {
			sb.append(dta.getData());
			if (!singleLine) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
