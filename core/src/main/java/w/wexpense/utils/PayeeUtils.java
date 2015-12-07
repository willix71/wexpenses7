package w.wexpense.utils;

import w.wexpense.model.City;
import w.wexpense.model.Payee;
import w.wexpense.model.PayeeType;

public class PayeeUtils {

	public static Payee newPayee(Object... args) {
		return fillPayee(new Payee(), args);
	}

	public static Payee fillPayee(Payee payee, Object... args) {
		int stringCount = 0;
		for (Object arg : args) {
			if (arg instanceof PayeeType) {
				payee.setType((PayeeType) arg);
			} else if (arg instanceof Payee) {
				payee.setBankDetails((Payee) arg);
			} else if (arg instanceof City) {
				payee.setCity((City) arg);
				// skip rest of address once the city is filled
				stringCount = 5;
			} else if (arg instanceof String) {
				stringCount++;
				if (payee.getName() == null) {
					payee.setName((String) arg); // first string is name
				} else if (payee.getAddress1() == null && stringCount <=2) {
					payee.setAddress1((String) arg);
				} else if (payee.getAddress2() == null && stringCount <=3) {
					payee.setAddress2((String) arg);
				} else if (payee.getPrefix() == null && stringCount <=4) {
					payee.setPrefix((String) arg);
				} else if (payee.getPostalAccount() == null && stringCount <=5) {
					payee.setPostalAccount((String) arg);
				} else if (payee.getIban() == null && stringCount <=6) {
					payee.setIban((String) arg);
				}
			}
		}
		payee.buildExternalReference();
		return payee;
	}
}
