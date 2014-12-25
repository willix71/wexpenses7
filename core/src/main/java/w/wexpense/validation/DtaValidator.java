package w.wexpense.validation;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import w.wexpense.dta.DtaFormater;
import w.wexpense.model.Expense;
import w.wexpense.utils.PaymentDtaUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

public class DtaValidator implements ConstraintValidator<Dtanized, Expense> {
	
	@Override
	public void initialize(Dtanized constraintAnnotation) {
		// void
	}

	@Override
	public boolean isValid(Expense expense, ConstraintValidatorContext context) {
		String formaterName = expense.getType().getPaymentGeneratorClassName();
		if (Strings.isNullOrEmpty(formaterName)) return true;

		DtaFormater formater = null;
		try {
			formater = (DtaFormater) PaymentDtaUtils.getDtaFormater(expense);
		} catch(Exception e) {
			ConstraintViolationBuilder vbuilder = context.buildConstraintViolationWithTemplate("Error creating formater");
			vbuilder.addNode("type.paymentGeneratorClassName");
			vbuilder.addConstraintViolation();
			return false;
		}
		
		Multimap<String,String> violation = formater.validate(expense);
		if (!violation.isEmpty()) {
			for(Map.Entry<String,String> entry: violation.entries()) {
				ConstraintViolationBuilder vbuilder = context.buildConstraintViolationWithTemplate(entry.getValue());
				// TODO
//				if (!Strings.isNullOrEmpty(entry.getKey())) {
//					for(String node: entry.getKey().split("\\.")) {
//						vbuilder.addNode(node);
//					}
//				}
				vbuilder.addConstraintViolation();
			}
			return false;
		}
		
		return true;		
	}
}
