package w.wexpense.dta;

import java.util.Map;

import com.google.common.collect.Multimap;

public class DtaException extends Exception {

    private static final long serialVersionUID = 1L;

    private Multimap<String, String> constraintViolations;
	
	public DtaException(String message) {
		super(message);
	}

	public DtaException(String message, Throwable cause) {
		super(message, cause);
	}	

	public DtaException(Multimap<String, String> constraintViolations) {
		super(getConstraintViolationsMessage(constraintViolations));
		this.constraintViolations = constraintViolations;
	}

	public Multimap<String, String> getConstraintViolations() {
		return constraintViolations;
	}
	
	public static String getConstraintViolationsMessage(Multimap<String, String> violations) {
		StringBuilder msg = new StringBuilder();
		for(Map.Entry<String,String> entry: violations.entries()) {
			msg.append("\n").append(entry.getKey()).append(": ").append(entry.getValue());
		}
		return msg.toString();
	}
}
