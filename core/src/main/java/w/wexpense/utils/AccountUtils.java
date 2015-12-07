package w.wexpense.utils;

import w.wexpense.model.Account;
import w.wexpense.model.Currency;
import w.wexpense.model.Payee;
import w.wexpense.model.enums.AccountEnum;

public class AccountUtils {

	public static void reNumber(Account account) {
		account.updateFullNameAndNumber();
		for (Account child : account.getChildren()) {
			reNumber(child);
		}
	}

	public static Account newAccount(Object... args) {
		return fillAccount(new Account(), args);
	}

	public static Account fillAccount(Account account, Object... args) {
		for (Object arg : args) {
			if (arg instanceof Account) {
				account.setParent((Account) arg);
			} else if (arg instanceof Currency) {
				account.setCurrency((Currency) arg);
			} else if (arg instanceof AccountEnum) {
				account.setType((AccountEnum) arg);
			} else if (arg instanceof Payee) {
				account.setOwner((Payee) arg);
			} else if (arg instanceof Number) {
				account.setNumber(((Number) arg).toString());
			} else if (arg instanceof String) {
				if (account.getName() == null) {
					account.setName((String) arg);
				} else if (account.getExternalReference() == null) {
					account.setExternalReference((String) arg);
				}
			}
		}
		account.updateFullNameAndNumber();
		return account;
	}

}
