package w.wexpense.dta;

import junit.framework.Assert;

import org.junit.Test;

import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Payee;

public class DtaHelperTest {

	@Test
	public void testZeroPadInt() {
		Assert.assertEquals("001", DtaHelper.zeroPad(1, 3));
		Assert.assertEquals("000002", DtaHelper.zeroPad(2, 6));
		Assert.assertEquals("000000003", DtaHelper.zeroPad(3, 9));
		
		try {
			DtaHelper.zeroPad(4, 10);
			Assert.fail("10 is to big a size");
		} catch(IllegalArgumentException ve) {
			// ok
		}
	}
	
	@Test
	public void testZeroPadString() {
		Assert.assertEquals("000", DtaHelper.zeroPad(null, 3));
		Assert.assertEquals("ello", DtaHelper.zeroPad("hello", 4));
		Assert.assertEquals("hello", DtaHelper.zeroPad("hello", 5));
		Assert.assertEquals("0hello", DtaHelper.zeroPad("hello", 6));
	}
	
	@Test
	public void testStripBlanks()	{
		Assert.assertEquals("00000000123456789", DtaHelper.stripBlanks("00 00000 01234 56789"));
		Assert.assertEquals("000123456789", DtaHelper.stripBlanks("00   01234 56789"));
	}
	
	public static Payee getWilliamsBankDetails() {
		Payee p = new Payee();
		p.setName("William Keyser");
		p.setAddress1("11 ch du Grand Noyer");
		p.setCity(new City("1197", "Prangins", new Country("CH", null, null)));
		p.setIban("CH650022822851333340B");
		return p;
	}
}
