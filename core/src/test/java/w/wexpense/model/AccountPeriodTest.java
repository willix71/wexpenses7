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

   @Test(expected = IllegalArgumentException.class)
   public void testValueOf200019() {
      new AccountPeriod(200019);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testValueOf200055() {
      new AccountPeriod(200055);
   }
   
   @Test
   public void testValueSemestrily() {
      AccountPeriod twenty = new AccountPeriod(200022);
      Assert.assertEquals(200022, twenty.intValue());
      Assert.assertEquals(twenty, AccountPeriod.valueOf(200022));
      Assert.assertEquals(twenty, AccountPeriod.valueOf(2000.22));
      Assert.assertEquals(twenty, AccountPeriod.valueOf("200022"));
      Assert.assertEquals(twenty, AccountPeriod.valueOf("2000.22"));
      Assert.assertEquals(twenty, AccountPeriod.valueOf("S2 2000"));
      Assert.assertEquals(twenty, AccountPeriod.valueOf("s2 2000"));
   }
   
   @Test
   public void testValueQuaterly() {
      AccountPeriod twenty = new AccountPeriod(200042);
      Assert.assertEquals(200042, twenty.intValue());
      Assert.assertEquals(twenty, AccountPeriod.valueOf("Q2 2000"));
      Assert.assertEquals(twenty, AccountPeriod.valueOf("q2 2000"));
   }
   
   @Test
   public void testValueOfYear() {
      int year = Calendar.getInstance().get(Calendar.YEAR);

      AccountPeriod dec = new AccountPeriod(year * 100 + 12);
      Assert.assertEquals(dec, AccountPeriod.valueOf(".12"));
      Assert.assertEquals(dec, AccountPeriod.valueOf("DEC"));

      AccountPeriod y = new AccountPeriod(year * 100);
      Assert.assertEquals(y, AccountPeriod.valueOf(" "));
      AccountPeriod m = new AccountPeriod(year * 100 + Calendar.getInstance().get(Calendar.MONTH) + 1);
      Assert.assertEquals(m, AccountPeriod.valueOf("."));
   }

   @Test
   public void testToString() {
      Assert.assertEquals("2000", new AccountPeriod(200000).toString());
      Assert.assertEquals("DEC 2000", new AccountPeriod(200012).toString());
      Assert.assertEquals("S1 2000", new AccountPeriod(200021).toString());
      Assert.assertEquals("T2 2000", new AccountPeriod(200032).toString());
      Assert.assertEquals("Q4 2000", new AccountPeriod(200044).toString());
   }

   @Test
   public void testTrickyValueOf200012() {
      AccountPeriod twenty = new AccountPeriod(200012);
      Assert.assertEquals(twenty, AccountPeriod.valueOf("DEC  2000"));
      Assert.assertEquals(twenty, AccountPeriod.valueOf(" dec 2000"));
   }
   
   @Test
   public void testNextPeriod() {
      Assert.assertEquals(200100, new AccountPeriod(200000).nextPeriod().intValue());
      
      Assert.assertEquals(200002, new AccountPeriod(200001).nextPeriod().intValue());
      Assert.assertEquals(200101, new AccountPeriod(200012).nextPeriod().intValue());
      
      Assert.assertEquals(200022, new AccountPeriod(200021).nextPeriod().intValue());
      Assert.assertEquals(200121, new AccountPeriod(200022).nextPeriod().intValue());
      
      Assert.assertEquals(200032, new AccountPeriod(200031).nextPeriod().intValue());
      Assert.assertEquals(200131, new AccountPeriod(200033).nextPeriod().intValue());
      
      Assert.assertEquals(200042, new AccountPeriod(200041).nextPeriod().intValue());
      Assert.assertEquals(200141, new AccountPeriod(200044).nextPeriod().intValue());
   }
}
