package w.wexpense.vaadin7;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public class ValidationHelper {
	protected static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public static <T> String validate(T o, Class<?>... groups) {
		Set<ConstraintViolation<T>> violations = validator.validate(o, groups);
		if (violations.isEmpty()) return null;

		StringBuilder msg = new StringBuilder();
		for (ConstraintViolation<?> violation : violations) {
			if (msg.length() > 0)  msg.append("\n");
			msg.append(violation.getPropertyPath().toString()).append(": ");
			msg.append(violation.getMessage());
		}
		return msg.toString();
	}

}
