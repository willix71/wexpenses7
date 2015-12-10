package w.wexpense.rest.intg.test;

import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {
	
	public static final String BASE_SVR = "http://localhost:"+System.getProperty("serverport", "8080");
	public static final String BASE_PATH = "/wexpenses-rest/rest";
	public static final String BASE_URI = BASE_SVR + BASE_PATH;
	
	@Test
	public void testNonIntegrationTest() {
		Assert.assertNull( System.getProperty("serverport") );
	}
}
