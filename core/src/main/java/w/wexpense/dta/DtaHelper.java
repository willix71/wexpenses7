package w.wexpense.dta;

import static w.wexpense.model.enums.TransactionLineEnum.IN;
import static w.wexpense.model.enums.TransactionLineEnum.OUT;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class DtaHelper {

	public static final String APPLICATION_ID = "WEX01";
		
	public static final String lineSeparator = ";";	
	
	private static final String[] BLANK = new String[128];
	static {		
		String b = "";
		for(int i=0;i<BLANK.length;i++) {
			BLANK[i]=b;
			b += " ";
		}
	}
	
	public static String pad(int size) {
		return BLANK[size];
	}

	public static String pad(Date date) {
		return MessageFormat.format("{0, date,yyMMdd}",date);
	}
	
	public static String pad(String text, int size) {
		if (text==null) {
			return BLANK[size];		
		} 
		int l = text.length();
		if (l == size) {
			return text;
		} else if (l > size) {
			return text.substring(0, size);
		} else {
			return text + BLANK[size-text.length()];
		}
	}

	/**
	 * Right pad text with 0 (stripping leading characters if text is two long)
	 */
	public static String zeroPad(String text, int size) {
		if (text==null) {
			return Strings.padStart("",size,'0');		
		} 
		int l = text.length();
		if (l == size) {
			return text;
		} else if (l > size) {
			return text.substring(l-size);
		} else {
			return Strings.padStart(text,size,'0');
		}
	}
	
	/**
	 * Right pad int with 0 (stripping leading numbers if text is two long)
	 * 
	 * Note maximum's size is 9
	 */
	public static String zeroPad(int i, int size) {
		Preconditions.checkArgument(size < 10, "Cannot zero pad more than 9 digits");
		
		int v = i % 1000000000;
		String s = new DecimalFormat("000000000").format(v);		
		return s.length()>size?s.substring(s.length()-size,s.length()):s;
	}
		
	/**
	 * Remove any blanks in the text
	 */
	public static String stripBlanks(String text) {
	    if (text==null) return null;
		String[] parts = text.split(" ");
		String spaceless = Joiner.on("").join(parts);
		return spaceless;		
	}
		
	public static List<String> split(String text, int maxLine) {
		List<String> lines;
		if (text == null)  {
			lines = new ArrayList<String>();
		} else if (text.contains(lineSeparator)) {
			String[] separated = text.split(Pattern.quote(lineSeparator));
			lines = Arrays.asList(separated);
		} else {
			lines = new ArrayList<String>();
			while(text.length() > 0) {
				int index = Math.min(maxLine, text.length());
				lines.add(text.substring(0,index));
				text.substring(index);			
			}
		}
		return lines;
	}
	
	public static String getExpenseHint(Expense expense) {
		return getTransactionLine(IN, expense).getAccount().getFullName();
	}
	
	public static TransactionLine getTransactionLine(TransactionLineEnum factor, Expense expense) {
		for(TransactionLine l: expense.getTransactions()) {
			if (l.getFactor()==factor) return l;
		}
		return null;
	}
	
	public static int getTransactionLineIndex(TransactionLineEnum factor, Expense expense) {
		int i = 0;
		for(TransactionLine l: expense.getTransactions()) {
			if (l.getFactor()==factor) return i;
			i++;
		}
		return -1;
	}
		
	public static String getHeader(String transactionType, Date paymentDate, int index, Expense expense, boolean dateInHeader) {
		StringBuilder sb = new StringBuilder();
		
		if (dateInHeader) {	
			sb.append(pad(expense.getDate()));
		} else {
			sb.append("000000");			
		}
		
		// clearing of the beneficiary's bank
		// TODO for 827 to bank payment, clearing of the beneficiary's bank if payment is made to a clearing bank
		sb.append(pad(12));
		
		// Bank reserved sequence
		sb.append("00000");
		
		// creation date
		if (paymentDate == null) {
			sb.append("000000");
		} else {
			sb.append(pad(paymentDate));
		}
		
		// clearing of the beneficiary's bank
		if (expense == null) {
			sb.append(pad(7));
		} else {
			String iban = getTransactionLine(OUT, expense).getAccount().getOwner().getIban();
			String clearing = iban.substring(4, 9);
			// remove leading zero and then pad to 7
			String purgedClearing = Integer.valueOf(clearing).toString();
			sb.append(pad(purgedClearing,7));
		}
		
		// sender id
		sb.append(pad(APPLICATION_ID,5));
		
		// sequence number
		sb.append(zeroPad(index, 5));
		
		// transaction type
		sb.append(pad(transactionType, 3));
		
		// is salary and processing flag
		sb.append("00");
		
		return sb.toString();
	}

	public static String formatLine01(String transactionType, Date paymentDate, int index, Expense expense, boolean dateInHeader) {
		StringBuilder line01 = new StringBuilder();
		line01.append("01");
		
		line01.append(getHeader(transactionType, paymentDate, index, expense, dateInHeader));
		line01.append(pad(APPLICATION_ID,5));
		line01.append("ID");
		line01.append(zeroPad(expense.getId().intValue(), 9));
		
		String iban = getTransactionLine(OUT, expense).getAccount().getOwner().getIban();
		line01.append(pad(iban,24));
		
		// date (blank mean as indicated in the header)		
		if (dateInHeader) {
			line01.append(pad(6));
		} else {
			line01.append(pad(expense.getDate()));			
		}
		// currency
		line01.append(pad(expense.getCurrency().getCode(),3));
		// amount
		String amount = new DecimalFormat("0.00").format(expense.getAmount()).replace(".", ",");
		line01.append(pad(amount,12));
		
		// reserved
		line01.append(pad(14));
		return line01.toString();
	}
	
	public static Multimap<String, String> commonValidation(Expense expense) {
		Multimap<String, String> errors = ArrayListMultimap.create();
		
		TransactionLine outLine = getTransactionLine(OUT, expense);
		int outIndex = getTransactionLineIndex(OUT, expense);
		if (outLine == null) {
			errors.put("transactions", "No OUT transaction line");
		} else if (outLine.getAccount() == null) {
			errors.put("transactions["+outIndex+"]", "Out transaction line must have an account");
		} else if (outLine.getAccount().getOwner() == null){
			errors.put("transactions["+outIndex+"]", "Out transaction line account must have an owner");		
		} else {
			if (Strings.isNullOrEmpty(outLine.getAccount().getOwner().getIban())){
				errors.put("transactions["+outIndex+"]", "Out transaction line account's owner must have an Iban");
			}
			if (expense.getPayee().getCity()==null) {
				errors.put("transactions["+outIndex+"]", "Out transaction line account's owner must have a city");
			}
		}
		
		TransactionLine inLine = getTransactionLine(IN, expense);
		int inIndex = getTransactionLineIndex(IN, expense);
		if (inLine == null) {
			errors.put("transactions", "No IN transaction line");
		} else if (inLine.getAccount() == null) {
			errors.put("transactions["+inIndex+"]", "IN transaction line must have an account");
		}
		
		if (expense.getPayee().getCity()==null) {
			errors.put("payee.city", "Payee's must have a city");
		}
		return errors;
	}
	
	public static String formatPostalAccount(Payee payee) {
		StringBuilder sb = new StringBuilder();
		String account = payee.getPostalAccount();
		if (payee.getBankDetails() != null) {
			sb.append(payee.getBankDetails().toString());
			if (account==null) {
				account = payee.getBankDetails().getPostalAccount();
			}
		}
		if (account != null) {
			sb.append("CP :").append(account);
		}
		return sb.toString();
	}
}
