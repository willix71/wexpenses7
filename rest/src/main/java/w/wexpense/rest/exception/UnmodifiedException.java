package w.wexpense.rest.exception;

public final class UnmodifiedException extends RuntimeException {

	private static final long serialVersionUID = 3691642349894840127L;

	public UnmodifiedException() {
        super();
    }

    public UnmodifiedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnmodifiedException(final String message) {
        super(message);
    }

    public UnmodifiedException(final Throwable cause) {
        super(cause);
    }

}
