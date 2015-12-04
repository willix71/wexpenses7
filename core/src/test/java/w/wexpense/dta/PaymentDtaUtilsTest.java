package w.wexpense.dta;

import static org.assertj.core.api.Assertions.assertThat;
import static w.wexpense.dta.DtaCommonTestData.createPaymentData;

import org.junit.Test;

import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;
import w.wexpense.utils.PaymentDtaUtils;


public class PaymentDtaUtilsTest {

	@Test
	public void testDtaLineLength() throws Exception {
		Payment payment = createPaymentData(15,2,2013,"test.dta", 
				BvoDtaFormaterTest.getBvoExpense(),
				BvrDtaFormaterTest.getBvrExpense(),
				IbanDtaFormaterTest.getIbanExpense()); 
		
		int i = 1;
		for (PaymentDta dta: PaymentDtaUtils.getPaymentDtas(payment)) {	
			String line = dta.getData();
			assertThat(line.length()).as("line %d [%s] lenght:",i++,line).isEqualTo(128);
		}
	}
}
