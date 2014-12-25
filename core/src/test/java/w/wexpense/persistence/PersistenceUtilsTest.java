package w.wexpense.persistence;

import org.junit.Assert;
import org.junit.Test;

import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.DBable;
import w.wexpense.persistence.PersistenceUtils;

public class PersistenceUtilsTest {

	@Test
	public void testGetIdName() {
		Assert.assertEquals("code", PersistenceUtils.getIdName(Currency.class));
		Assert.assertEquals("id", PersistenceUtils.getIdName(DBable.class));
		Assert.assertEquals("id", PersistenceUtils.getIdName(City.class));
	}
	
	@Test
	public void test() {
		Country ch = new Country("ch", "swiss", null);
		Assert.assertEquals("ch", PersistenceUtils.getIdValue(ch));
		
		City prangins = new City("1197", "Prangins", ch);
		prangins.setId(1000l);
		Assert.assertEquals(1000l, PersistenceUtils.getIdValue(prangins));
	}
}
