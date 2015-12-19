package w.wexpense.dta;

import static w.wexpense.dta.DtaHelper.formatLine01;
import static w.wexpense.dta.DtaHelper.lineSeparator;
import static w.wexpense.dta.DtaHelper.pad;
import static w.wexpense.dta.DtaHelper.stripBlanks;
import static w.wexpense.model.enums.TransactionLineEnum.OUT;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;
import w.wexpense.validation.IbanValidator;

public class IbanDtaFormater implements DtaFormater {

	public static final String TRANSACTION_TYPE = "836";
		
	@Override
	public List<String> format(Payment payment, int index, Expense expense)  throws DtaException {
		Multimap<String, String> violations = validate(expense);
		if (!violations.isEmpty()) {
			throw new DtaException(violations);
		}
		
		List<String> lines = new ArrayList<String>();
		lines.add(formatLine01(TRANSACTION_TYPE, payment.getDate(), index, expense, false));
		lines.add(formatLine02(payment, index, expense));
		lines.add(formatLine03(payment, index, expense));
		lines.add(formatLine04(payment, index, expense));
		lines.add(formatLine05(payment, index, expense));
		return lines;
	}

	@Override
	public Multimap<String, String> validate(Expense expense) {
		Multimap<String, String> errors = DtaHelper.commonValidation(expense);
		if (Strings.isNullOrEmpty(expense.getPayee().getIban())) {
			errors.put("payee.postalAccount", "Payee's Iban is mandatory for BVO payments (836)");
		}
		if (expense.getPayee().getBankDetails()==null) {
			errors.put("payee.bankDetails", "Payee's must have a bank details");
		} else if (expense.getPayee().getBankDetails().getCity()==null) {
			errors.put("payee.bankDetails.city", "Payee's bank details must have a city");
		}
		return errors;
	}		
	
	@Override
    public String payee(Expense expense) {
		Payee payee = expense.getPayee();
        Joiner j = Joiner.on('\n').skipNulls();
        return j.join(               
                payee.toString(),
                IbanValidator.formatIban(payee.getIban()),
                payee.getBankDetails().toString()); 
    }
	   
	protected String formatLine02(Payment payment, int index, Expense expense) {
		StringBuilder line02 = new StringBuilder();

			line02.append("02");
			
			// conversation rate if agreed
			line02.append(pad(12));
			
			Payee payee = DtaHelper.getTransactionLine(OUT, expense).getAccount().getOwner();
			line02.append(pad(payee.getName(),35));
			line02.append(pad(payee.getAddress1(),35));
			line02.append(pad(payee.getCity().toString(),35));

			line02.append(pad(DtaHelper.getExpenseHint(expense),9));
			return line02.toString();
	}
	
	protected String formatLine03(Payment payment, int index, Expense expense) {
		StringBuilder line03 = new StringBuilder();
		line03.append("03");
		
		// A or D
		line03.append("D");
		
		// beneficiary
		Payee institution = expense.getPayee().getBankDetails();
		line03.append(pad(institution.getPrefixedName(), 35));
		line03.append(pad(institution.getCity().toString(), 35));

		// IBAN
		line03.append(pad(stripBlanks(expense.getPayee().getIban()),34));

		line03.append(pad(21));
		return line03.toString();
	}
	
	protected String formatLine04(Payment payment, int index, Expense expense) {
		StringBuilder line04 = new StringBuilder();
		line04.append("04");
				
		// Beneficiary
		line04.append(pad(expense.getPayee().getPrefixedName(), 35));
		line04.append(pad(expense.getPayee().getAddress1(), 35));
		line04.append(pad(expense.getPayee().getCity().toString(), 35));
		
		line04.append(pad(21));
		return line04.toString();
	}
	
	protected String formatLine05(Payment payment, int index, Expense expense) {
		StringBuilder line05 = new StringBuilder();
		line05.append("05");
				
		// I or U
		line05.append("U");
		
		// purpose
		String externalReference = expense.getExternalReference();		
		if (externalReference == null || !externalReference.contains(lineSeparator))  {
			line05.append(pad(externalReference, 105));
		} else {
			String[] separated = externalReference.split(Pattern.quote(lineSeparator));
			for(int i=0;i<4;i++) {
				if (separated.length > i) {
					line05.append(pad(separated[i],35));
				} else {
					line05.append(pad(35));
				}
			}
		}
		
		// Rules for charges
		// 0=OUR=All charges debited to the ordering party
		// 1=BEN=All charges debited to the beneficiary
		// 2=SHA=charges split
		line05.append("2");
		
		line05.append(pad(19));
		return line05.toString();
	}
}
