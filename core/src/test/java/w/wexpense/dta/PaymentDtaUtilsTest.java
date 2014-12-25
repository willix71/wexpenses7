package w.wexpense.dta;

import static w.wexpense.dta.DtaCommonTestData.createPaymentData;
import junit.framework.Assert;

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
			//System.out.println(line + "]]");
			Assert.assertEquals("line "+i+"'s length is not 128",128, line.length());
		}
	}
}
