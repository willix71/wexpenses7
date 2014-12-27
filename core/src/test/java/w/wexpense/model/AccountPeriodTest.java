package w.wexpense.model;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

public class AccountPeriodTest {

	@Test
	public void testBasics() {
		AccountPeriod twenty = new AccountPeriod(200000);
		Assert.assertEquals(200000, twenty.intValue());
		AccountPeriod klone = twenty.klone();
		Assert.assertNotSame(twenty, klone);
		Assert.assertEquals(twenty, klone);
	}
	
	@Test
	public void testValueOf200000() {
		AccountPeriod twenty = new AccountPeriod(200000);
		Assert.assertEquals(twenty, AccountPeriod.valueOf(200000));
		Assert.assertEquals(twenty, AccountPeriod.valueOf(2000.00));
		Assert.assertEquals(twenty, AccountPeriod.valueOf("2000"));
		Assert.assertEquals(twenty, AccountPeriod.valueOf("200000"));
		Assert.assertEquals(twenty, AccountPeriod.valueOf("2000.00"));
	}
	
	@Test
	public void testValueOf200012() {
		AccountPeriod twenty = new AccountPeriod(200012);
		Assert.assertEquals(200012, twenty.intValue());
		Assert.assertEquals(twenty, AccountPeriod.valueOf(200012));
		Assert.assertEquals(twenty, AccountPeriod.valueOf(2000.12));
		Assert.assertEquals(twenty, AccountPeriod.valueOf("200012"));
		Assert.assertEquals(twenty, AccountPeriod.valueOf("2000.12"));
		Assert.assertEquals(twenty, AccountPeriod.valueOf("DEC 2000"));
		Assert.assertEquals(twenty, AccountPeriod.valueOf("dec 2000"));
	}
	
	@Test
	public void testValueOfYear() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		
		
		AccountPeriod dec = new AccountPeriod(year * 100 + 12);
		Assert.assertEquals(dec, AccountPeriod.valueOf(".12"));
		Assert.assertEquals(dec, AccountPeriod.valueOf("DEC"));
		
		AccountPeriod y = new AccountPeriod(year * 100);
		Assert.assertEquals(y, AccountPeriod.valueOf(" "));
		AccountPeriod m = new AccountPeriod(year * 100 + Calendar.getInstance().get(Calendar.MONTH) +1);
		Assert.assertEquals(m, AccountPeriod.valueOf("."));
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("2000", new AccountPeriod(200000).toString());
		Assert.assertEquals("DEC 2000", new AccountPeriod(200012).toString());
	}
	
	@Test
	public void testTrickyValueOf200012() {
		AccountPeriod twenty = new AccountPeriod(200012);
		Assert.assertEquals(twenty, AccountPeriod.valueOf("DEC  2000"));
		Assert.assertEquals(twenty, AccountPeriod.valueOf(" dec 2000"));
		
		
	}
}
