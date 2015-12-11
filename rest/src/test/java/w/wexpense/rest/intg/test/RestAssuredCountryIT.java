package w.wexpense.rest.intg.test;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static w.wexpense.rest.intg.test.ConfigTest.BASE_URI;

import org.junit.Test;
import org.springframework.core.annotation.Order;

/**
 * This test validates Codable entities.
 * 
 * @author willix
 *
 */
public class RestAssuredCountryIT extends AbstractRestAssured {

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
	@Order(1)
	public void testGetAllUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json"). 
	    body("code", hasItems("CH","UK","FR","IT","DE","US")).
	    when().get("/country");
	}
	
	@Test
	@Order(2)
	public void testGetPagedUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    header("Link", containsString("next")).
	    header("Link", containsString("last")).
	    header("Link", not(containsString("prev"))).
	    header("Link", not(containsString("first"))).
	    body("code", hasItems("CH","DE","FR")).
	    when().get("/country?page=0&size=3&orderBy=code");
	  
	  expect().
	    statusCode(200).
	    header("Link", not(containsString("next"))).
	    header("Link", not(containsString("last"))).
	    header("Link", containsString("prev")).
	    header("Link", containsString("first")).
	    body("code", hasItems("IT","UK","US")).
	    when().get("/country?page=1&size=3&orderBy=code");
	  
	  // no more pages
	  expect().statusCode(404).when().get("/country?page=2&size=3&orderBy=code");
	}
	
	@Test
	@Order(3)
	public void testPostCreateUser() {
	  expect().
	    statusCode(201).
	    header("Location", BASE_URI + "/country/WK").
	    when().given().
	    header("Content-Type", "application/json").
	    body("{\"code\":\"WK\",\"name\":\"Williams country\",\"currency\": {\"code\":\"CHF\"}}").
	    post("/country");
	  
	  expect().statusCode(200).when().get("/country/WK");
	}

	@Test
	@Order(4)
	public void testPutUpdateUser() {
		expect().statusCode(200).
		body("code", equalTo("WK"), "name", equalTo("Williams country"), "currency.code", equalTo("CHF"), "currency.name", equalTo("Swiss Francs")).
		when().get("/country/WK");

		expect().statusCode(200).when().given().header("Content-Type", "application/json")
				.body("{\"code\":\"WK\",\"name\":\"My country\",\"currency\": {\"code\":\"USD\"}}").put("/country/WK");

		expect().statusCode(200).
		body("code", equalTo("WK"), "name", equalTo("My country"), "currency.code", equalTo("USD"), "currency.name", equalTo("US Dollar")).
		when().get("/country/WK");
	}
	
	@Test
	@Order(5)
	public void testPatchUpdateUser() {
		expect().statusCode(200).
		body("code", equalTo("WK"), "name", equalTo("My country"), "currency.code", equalTo("USD"), "currency.name", equalTo("US Dollar")).
		when().get("/country/WK");

		expect().statusCode(204).when().given().header("Content-Type", "application/json")
				.body("{\"name\":\"My other country\"}").patch("/country/WK");

		expect().statusCode(200).
		body("code", equalTo("WK"), "name", equalTo("My other country"), "currency.code", equalTo("USD"), "currency.name", equalTo("US Dollar")).
		when().get("/country/WK");
	}
	
	@Test
	@Order(6)
	public void testGetDeleteUser() {
		// created at previous test
		expect().statusCode(200).when().get("/country/WK");

		expect().statusCode(200).when().delete("/country/WK");

		expect().statusCode(404).when().get("/country/WK");
	}
}
