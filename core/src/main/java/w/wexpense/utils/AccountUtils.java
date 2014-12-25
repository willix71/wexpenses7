package w.wexpense.utils;

import w.wexpense.model.Account;

public class AccountUtils {

	public static void reNumber(Account account) {
		account.updateFullNameAndNumber();
		for(Account child: account.getChildren()) {
			reNumber(child);
		}
	}

}
