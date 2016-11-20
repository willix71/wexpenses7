package w.wexpense.dta;

import static w.wexpense.dta.DtaCommonTestData.createPaymentData;

import org.junit.Assert;
import org.junit.Test;

import w.wexpense.model.Payment;

public class EndDtaFormaterTest {

	final String expected = "01000000            00000130215       WEX0100002890000,00                                                                       ";
	
	@Test
	public void testBvo() {
		Payment payment = createPaymentData(15,2,2013,"test.dta"); 
		String l = new EndDtaFormater().format(payment, 2);
		Assert.assertNotNull(l);
		Assert.assertEquals(128, l.length());
		Assert.assertEquals(expected, l);
	}
}
