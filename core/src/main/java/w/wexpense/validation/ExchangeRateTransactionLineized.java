package w.wexpense.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ExchangeRateTransactionLineValidator.class)
public @interface ExchangeRateTransactionLineized {

	/**
	 * Can be overriden with a custom message by using curly brackers
	 * i.e.: "{w.wexpense.validation.transaction.total}"
	 */
	String message() default "Invalid exchange rate for transaction line";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
