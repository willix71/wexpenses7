package w.wexpense.rest.intg.test;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

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
	    statusCode(200). // default mapping is json
	    contentType("application/json").
	    body(
	      "code", equalTo("CH"),
	      "name", equalTo("Switzerland"),
	      "currency.code",equalTo("CHF"),
	      "currency.name",equalTo("Swiss Francs")).
	    when().get("/country/CH");
	}
	
	@Test
	public void testGetUnknownUser() {
	  expect().
	    statusCode(404). // default mapping is json
	    when().get("/country/xx");
	}
	
	@Test
	public void testGetAllUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json"). 
	    body("code", hasItems("CH","UK","FR","IT","DE","US")).
	    when().get("/country");
	}
	
	@Test
	public void testGetPagedUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    body("code", hasItems("CH","DE","FR")).
	    when().get("/country?page=0&size=3&orderBy=code");
	  
	  expect().
	    statusCode(200).
	    body("code", hasItems("IT","UK","US")).
	    when().get("/country?page=1&size=3&orderBy=code");
	  
	  expect().statusCode(404).when().get("/country?page=2&size=3&orderBy=code");
	}
	
	@Test
	public void testGetCreateUser() {
	  expect().
	    statusCode(201).
	    when().given().
	    header("Content-Type", "application/json").
	    body("{\"code\":\"WK\",\"name\":\"Williams country\",\"currency\": {\"code\":\"CHF\"}}").
	    post("/country");
	}
	
}
