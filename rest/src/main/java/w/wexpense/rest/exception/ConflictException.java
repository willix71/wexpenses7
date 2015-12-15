package w.wexpense.rest.exception;

public class ConflictException extends RuntimeException {

	private static final long serialVersionUID = 3691642349894840127L;

	final String field;
	final Object oldValue;
	final Object newValue;
	
	public ConflictException(String field, Object oldValue, Object newValue) {
		super("None matching " + field);
		this.field = field;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
}
