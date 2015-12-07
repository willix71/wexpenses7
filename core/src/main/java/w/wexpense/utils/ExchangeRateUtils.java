package w.wexpense.utils;

import java.util.Date;

import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Payee;

public class ExchangeRateUtils {

	public static ExchangeRate newExchangeRate(Object... args) {
		return fillExchangeRate(new ExchangeRate(), args);
	}

	public static ExchangeRate fillExchangeRate(ExchangeRate rate, Object... args) {
		for (Object arg : args) {
			if (arg instanceof Payee) {
				rate.setInstitution((Payee) arg);
			} else if (arg instanceof Date) {
				rate.setDate((Date) arg);
			} else if (arg instanceof Currency) {
				if (rate.getFromCurrency() == null) {
					rate.setFromCurrency((Currency) arg);
				} else if (rate.getToCurrency() == null) {
					rate.setToCurrency((Currency) arg);
				}
			} else if (arg instanceof Number) {
				if (rate.getRate() == null) {
					rate.setRate(((Number) arg).doubleValue());
				} else if (rate.getFee() == null) {
					rate.setFee(((Number) arg).doubleValue());
				} else if (rate.getFixFee() == null) {
					rate.setFixFee(((Number) arg).doubleValue());
				}
			}
		}
		return rate;
	}
}
