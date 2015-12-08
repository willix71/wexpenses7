package w.wexpense.rest.intg.test;

import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {

	public static final String BASE_URI = "http://localhost:"+System.getProperty("serverport", "8080") +"/wexpenses-rest/rest/";
	
	@Test
	public void testNonIntegrationTest() {
		;
		Assert.assertNull( System.getProperty("serverport") );
	}
}
