package w.wexpense.rest.intg.test;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.RestAssured;
public class CountryRestAssuredIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(CountryRestAssuredIT.class);
		
	@BeforeClass
	public static void setUp() {
		RestAssured.baseURI = ConfigTest.BASE_SVR;
		RestAssured.basePath = ConfigTest.BASE_PATH;
		
		LOGGER.info("baseURI and basePath set to " + ConfigTest.BASE_URI);
	}
	
	@Test
	public void testGetSingleUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    body(
	      "code", equalTo("CH"),
	      "name", equalTo("Switzerland"),
	      "currency.code",equalTo("CHF"),
	      "currency.name",equalTo("Swiss Francs")).
	    when().
	    with().header("Accept","application/json").
	    get("/country/CH");
	}
}
