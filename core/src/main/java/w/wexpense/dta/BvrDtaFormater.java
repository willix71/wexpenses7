package w.wexpense.dta;

import static w.wexpense.dta.DtaHelper.formatLine01;
import static w.wexpense.dta.DtaHelper.lineSeparator;
import static w.wexpense.dta.DtaHelper.pad;
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

public class BvrDtaFormater implements DtaFormater {

	public static final String TRANSACTION_TYPE = "827";
		
	@Override
	public List<String> format(Payment payment, int index, Expense expense) throws DtaException {
		Multimap<String, String> violations = validate(expense);
		if (!violations.isEmpty()) {
			throw new DtaException(violations);
		}
		
		List<String> lines = new ArrayList<String>();
		lines.add(formatLine01(TRANSACTION_TYPE, payment.getDate(), index, expense, true));
		lines.add(formatLine02(payment, index, expense));
		lines.add(formatLine03(payment, index, expense));
		lines.add(formatLine04(payment, index, expense));
		return lines;
	}
	
	@Override
	public Multimap<String, String> validate(Expense expense) {
		Multimap<String, String> errors = DtaHelper.commonValidation(expense);
		if (getBeneficiaryAccount(expense.getPayee())==null) {			
			String msg = "Either the payee's postal account or the payee's bank detail postal account are needed for BVR payments (827)";
			errors.put("payee.postalAccount", msg);
			errors.put("payee.bankDetails.postalAccount", msg);
		}
		
		return errors;
	}		
	
	@Override
    public String payee(Expense expense) {
		Payee payee = expense.getPayee();

		Joiner j = Joiner.on('\n').skipNulls();
	    return j.join(payee.toString(), IbanValidator.formatIban(payee.getIban()), DtaHelper.formatPostalAccount(payee));   
   }
   
	protected String getProcessingDate(Expense expense) {
		return pad(expense.getDate());
	}
	
	protected String formatLine02(Payment payment, int index, Expense expense) {
		StringBuilder line02 = new StringBuilder();
		line02.append("02");
		
		Payee payee = DtaHelper.getTransactionLine(OUT, expense).getAccount().getOwner();
		line02.append(pad(payee.getName(),24));
		line02.append(pad(payee.getAddress1(),24));
		line02.append(pad(payee.getAddress2(),24));
		line02.append(pad(payee.getCity().toString(),24));

		line02.append(pad(DtaHelper.getExpenseHint(expense),30));
		return line02.toString();
	}
	
	protected String formatLine03(Payment payment, int index, Expense expense) {
		StringBuilder line03 = new StringBuilder();
		line03.append("03");
		
		line03.append("/C/").append(pad(getBeneficiaryAccount(expense.getPayee()),27));
		
		if (Strings.isNullOrEmpty(expense.getPayee().getIban())) {
    		line03.append(pad(expense.getPayee().getPrefixedName(), 24));
    		line03.append(pad(expense.getPayee().getAddress1(), 24));
    		line03.append(pad(expense.getPayee().getAddress2(), 24));
		} else {
		    line03.append(pad(DtaHelper.stripBlanks(expense.getPayee().getIban()), 24));
            line03.append(pad(expense.getPayee().getPrefixedName(), 24));
            line03.append(pad(expense.getPayee().getAddress1(), 24));
		}
		line03.append(pad(expense.getPayee().getCity().toString(), 24));
		
		return line03.toString();
	}
	
	protected String formatLine04(Payment payment, int index, Expense expense) {
		StringBuilder line04 = new StringBuilder();
		line04.append("04");
		
		// description
		String externalReference = expense.getExternalReference();		
		if (externalReference == null || !externalReference.contains(lineSeparator))  {
			line04.append(pad(externalReference, 112));
		} else {
			String[] separated = externalReference.split(Pattern.quote(lineSeparator));
			for(int i=0;i<4;i++) {
				if (separated.length > i) {
					line04.append(pad(separated[i],28));
				} else {
					line04.append(pad(28));
				}
			}
		}
		
		line04.append(pad(14));
		return line04.toString();
	}
	
	public static String getBeneficiaryAccount(Payee payee) {
		if (!Strings.isNullOrEmpty(payee.getPostalAccount())) {
			return payee.getPostalAccount();
		} else if (payee.getBankDetails()!=null && !Strings.isNullOrEmpty(payee.getBankDetails().getPostalAccount())) {
			return payee.getBankDetails().getPostalAccount();
		}
		return null;
	}

}
