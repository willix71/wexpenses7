package w.wexpense.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Assert;
import org.junit.Test;

/**
 * http://java.dzone.com/articles/bean-validation-and-jsr-303
 *
 */
public class IbanizedTest extends AbstractValidationTest {
	
	class Account {
		@Ibanized
		String iban;

		public Account(String iban) {
	      this.iban = iban;
      }		
	}
	
	public void good(String iban) {
		super.good(iban, new Account(iban));
	}
	
	public Set<ConstraintViolation<Account>> bad(String iban) {
		return super.bad(iban, new Account(iban));
	}
	
	@Test
	public void testGoodIbanValidator() {
		good("CH6580485000003213174");
		good("GB87BARC20658244971655");
		good("BE43068999999501");
		good("GB29 NWBK 6016 1331 9268 19");
		good("IL62-0108-0000-0009-9999-999");
		good("SA03.8000.0000.6080.1016.7519");
		good("CH93 0076     2011     6238     5295	7");
		
		good("CH2200767000Z50223861");
	}
	
	@Test
	public void testBadIbanValidator() {
		bad("CH6480485000003213174");
		bad("CH00");
	}
	
	@Test
	public void testIbanFormater() {
		Assert.assertEquals("CH84 0076 7000 T093 0649 7", IbanValidator.formatIban("CH84 0076 7000 T093 0649 7"));
		Assert.assertEquals("CH84 0076 7000 T093 0649 7", IbanValidator.formatIban("C H840 0767 000T 0930 6497"));
		Assert.assertEquals("CH84 0076 7000 T093 0649 7", IbanValidator.formatIban("CH8400767000T09306497"));
		Assert.assertEquals("", IbanValidator.formatIban(""));
		Assert.assertNull(IbanValidator.formatIban(null));
	}
	
	@Test
	public void testGoodPostalIbanValidator() {
		bad("CH63 0900 0000 1500 3457 9");
		good("CH23 0900 0000 1500 3457 9");
	}
}
