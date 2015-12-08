package w.wexpense.rest.intg.test;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
public class CountryRestAssuredIT {

	//@Before
	public void setUp() {
		RestAssured.baseURI = ConfigTest.BASE_SVR;
		RestAssured.basePath = ConfigTest.BASE_PATH;
	}
	
	//@Test
	public void testGetSingleUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    body(
	      "code", equalTo("CH"),
	      "name", equalTo("Switzerland")).
	    when().
	    header("Accept","application/json").
	    get("/country/CH");
	}
}
