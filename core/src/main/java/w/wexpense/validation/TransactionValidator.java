package w.wexpense.validation;

import java.math.BigDecimal;
import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import w.wexpense.model.TransactionLine;
import w.wexpense.utils.TransactionLineUtils;

public class TransactionValidator implements ConstraintValidator<Transactionized, Collection<TransactionLine>> {

	@Override
	public void initialize(Transactionized constraintAnnotation) {
		// void
	}

	@Override
	public boolean isValid(Collection<TransactionLine> lines, ConstraintValidatorContext context) {
		if (lines!=null && lines.size()>=2) {
			BigDecimal[] total = TransactionLineUtils.getAmountDeltaAndTotals(lines);
			return total[1].equals(total[2]);
		}
		return false;
	}
}