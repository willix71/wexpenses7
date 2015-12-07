package w.wexpense.utils;

import w.wexpense.model.City;
import w.wexpense.model.Payee;
import w.wexpense.model.PayeeType;

public class PayeeUtils {

	public static Payee newPayee(Object... args) {
		return fillPayee(new Payee(), args);
	}

	public static Payee fillPayee(Payee payee, Object... args) {
		for (Object arg : args) {
			if (arg instanceof Payee) {
				payee.setBankDetails((Payee) arg);
			} else if (arg instanceof PayeeType) {
				payee.setType((PayeeType) arg);
			} else if (arg instanceof City) {
				payee.setCity((City) arg);
			} else if (arg instanceof String) {
				if (payee.getName() == null) {
					payee.setName((String) arg); // first string is name
				}
				if (payee.getCity() == null) { 
					// fill in address as long as city is null
					if (payee.getAddress1() == null) {
						payee.setAddress1((String) arg);
					} else if (payee.getAddress2() == null) {
						payee.setAddress2((String) arg);
					} else if (payee.getPrefix() == null) {
						payee.setPrefix((String) arg);
					}
				} else {
					// fill banking detail once the city is filled
					if (payee.getPostalAccount() == null) {
						payee.setPostalAccount((String) arg);
					} else if (payee.getIban() == null) {
						payee.setIban((String) arg);
					}
				}
			}
		}
		return payee;
	}
}
