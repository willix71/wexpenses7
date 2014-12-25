package w.wexpense.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Assert;
import org.junit.Before;

public abstract class AbstractValidationTest {

	protected Validator validator;

	@Before
	public void setup() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}
		
	public void good(String message, Object o) {
		Assert.assertTrue(message, validator.validate(o).isEmpty());
	}
	
	public <T> Set<ConstraintViolation<T>> bad(String message, T o) {
		Set<ConstraintViolation<T>> viol = validator.validate(o);
		Assert.assertFalse(message, viol.isEmpty());
		return viol;
	}
}
