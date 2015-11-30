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

/**
 * An expense validator.
 * 
 * If the payment is being paid (rather that has been paid), we check that their is a 
 * payment set and that the expense is valid according to the DTA formater.
 *
 */
public class DtaValidator implements ConstraintValidator<Dtanized, Expense> {

   @Override
   public void initialize(Dtanized constraintAnnotation) {
      // void
   }

   @Override
   public boolean isValid(Expense expense, ConstraintValidatorContext context) {
      boolean isValid = true;

      String formaterName = expense.getType().getPaymentGeneratorClassName();
      if (!Strings.isNullOrEmpty(formaterName)) {
         
         if (expense.getPayment() == null) {
            ConstraintViolationBuilder vbuilder = context.buildConstraintViolationWithTemplate("No payment was selected");
            vbuilder.addNode("payment");
            vbuilder.addConstraintViolation();
            isValid = false;
         }

         DtaFormater formater = null;
         try {
            formater = (DtaFormater) PaymentDtaUtils.getDtaFormater(expense);
         } catch (Exception e) {
            ConstraintViolationBuilder vbuilder = context.buildConstraintViolationWithTemplate("Error creating formater");
            vbuilder.addNode("type.paymentGeneratorClassName");
            vbuilder.addConstraintViolation();
            isValid = false;
         }

         if (formater != null) {
            Multimap<String, String> violation = formater.validate(expense);
            if (!violation.isEmpty()) {
               isValid = false;

               for (Map.Entry<String, String> entry : violation.entries()) {
                  ConstraintViolationBuilder vbuilder = context.buildConstraintViolationWithTemplate(entry.getValue());

                  if (!Strings.isNullOrEmpty(entry.getKey())) {
                     for (String node : entry.getKey().split("\\.")) {
                        vbuilder.addNode(node);
                     }
                  }
                  vbuilder.addConstraintViolation();
               }
            }
         }
      }

      return isValid;
   }
}
