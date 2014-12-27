package w.wexpense.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AccountPeriod extends Number implements Serializable, Comparable<AccountPeriod>, Klonable<AccountPeriod> {

	private static final long serialVersionUID = 1L;

	private static final List<String> MONTHS = Arrays.asList("","JAN","FEB","MAR","APR","MAI","JUN","JUL","AUG","SEP","OCT","NOV","DEC");
	private static final String[] MONTHS_SPACE = {"","JAN ","FEB ","MAR ","APR ","MAI ","JUN ","JUL ","AUG ","SEP ","OCT ","NOV ","DEC "};
	
	private int period;

	public AccountPeriod(int value) {
		this.period = value;
	}
	
	@Override
	public double doubleValue() {
		return period;
	}

	@Override
	public float floatValue() {
		return period;
	}

	@Override
	public int intValue() {
		return period;
	}

	@Override
	public long longValue() {
		return period;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public AccountPeriod klone() {
		try {
			return (AccountPeriod) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Can not clone " + this.getClass().getName(), e);
		}
	}

	@Override
	public int compareTo(AccountPeriod other) {
		if (other==null) return -1;
		
		return other.period-period;
	}

	@Override
	public String toString() {
		return MONTHS_SPACE[period%100] + (period/100);
	}

	@Override
	public int hashCode() {
		return period;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountPeriod other = (AccountPeriod) obj;
		return period == other.period;
	}
	
	public static AccountPeriod valueOf(Integer value) {
		if (value == null) return null;
		else return new AccountPeriod(value);
	}

	public static AccountPeriod valueOf(Double value) {
		if (value == null) return null;
		else return new AccountPeriod((int) (value.doubleValue() * 100));
	}
	
	public static AccountPeriod valueOf(String value) {
		if (value == null || value.length()==0) return null;
		
		value = value.trim();
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month =0;
		
		if (value.length()>0) {
			if (value.equals(".")) {
				month = c.get(Calendar.MONTH) + 1;
			} else if (value.contains(".")){
				String parts[] = value.split("\\.");
				if (parts[0].length()>0) year = Integer.parseInt(parts[0]);
				if (parts[1].length()>0) month = Integer.parseInt(parts[1]);
			} else if (value.contains(" ")) {
				String parts[] = value.split("\\s+");
				int index = MONTHS.indexOf(parts[0].toUpperCase());
				if (index > 0) {
					month = index;
				}
				year = Integer.parseInt(parts[1]);
			} else  {
				int index = MONTHS.indexOf(value.toUpperCase());
				if (index > 0) {
					month = index;
				} else {
					int period = Integer.parseInt(value);				
					if (period <100000) {
						year = period;
					} else {
						month = period % 100;
						year = period /100;
					}
				}
			}
			
			if (year < 0 || month < 0 || month > 12) {
				throw new IllegalArgumentException(String.format("Can't convert '%s' to an AccountPeriod", value));
			}
		}
		
		return new AccountPeriod(year*100 + month);
	}
}
