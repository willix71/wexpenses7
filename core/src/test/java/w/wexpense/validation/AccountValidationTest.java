package w.wexpense.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;

import w.wexpense.model.Payee;

public class AccountValidationTest extends AbstractValidationTest {

	public void good(Payee payee) {
		super.good(payee.getName(), payee);
	}
	
	public Set<ConstraintViolation<Payee>> bad(Payee payee) {
		return super.bad(payee.getName(), payee);
	}
	
	@Test
	public void testSimpleGood() {
		Payee p = new Payee();
		p.setName("test");
		good(p);
		
		p.setName("testPostalAccount1-1-1");
		p.setPostalAccount("1-1-1");		
		good(p);
		
		p.setName("testIbanCH6580485000003213174");
		p.setIban("CH6580485000003213174");
		good(p);
	}
	
	@Test
	public void testBadPostalAccount() {
		Payee p = new Payee();
		p.setName("test1-1");
		p.setPostalAccount("1-1");
		bad(p);
		
		p.setName("test1-123456789-1");
		p.setPostalAccount("1-123456789-1");		
		bad(p);
	}
}
