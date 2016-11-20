package w.wexpense.rest.intg.test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static w.wexpense.rest.intg.test.ConfigTest.BASE_URI;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.annotation.Order;

import com.jayway.restassured.response.Response;

/**
 *  This test validates passing dates.
 *  
 * @author willix
 *
 */
public class RestAssuredExchangeRateIT extends AbstractRestAssured {

	private static final int xRateId = 25;

	@Test
	public void testGetSingleUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    body(
	      "fromCurrency.code", equalTo("EUR"),
	      "toCurrency.code", equalTo("CHF"),
	      "rate",equalTo(1.6f), // a float ???
	      "date",equalTo("20000101 000000")).
	    when().get("/exchangeRate/" + xRateId);
	}
	
	@Test
	public void testGetSingleUserByUid() {
		
		Response response = 
				when().get("/exchangeRate/" + xRateId).
				then().statusCode(200).extract().response();
		
		String uid = response.path("uid"); // extract uid
		
		expect().
	    statusCode(200).
	    body("id", equalTo(25)).
	    when().get("/exchangeRate?uid="+ uid);
	}

	@Test
	public void testGetUnknownUser() {
	  expect().
	    statusCode(404). // default mapping is json
	    when().get("/exchangeRate/999999");
	}
	
	@Test
	@Order(1)
	public void testGetAllUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json"). 
	    body("name", iterableWithSize(3)).
	    when().get("/exchangeRate");
	}
	
	@Test
	@Order(2)
	public void testGetPagedUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    header("Link", not(containsString("next"))).
	    header("Link", not(containsString("last"))).
	    header("Link", not(containsString("prev"))).
	    header("Link", not(containsString("first"))).
	    body("code", iterableWithSize(3)).
	    when().get("/exchangeRate?page=0");
	    
	  expect().statusCode(404).when().get("/exchangeRate?page=1");
	}
	
	@Test
	@Order(3)
	public void testGetCreateUser() {
	  expect().
	    statusCode(201).
	    header("Location", startsWith(BASE_URI + "/exchangeRate/")).
	    when().given().
	    header("Content-Type", "application/json").
	    body("{\"rate\":1.234,\"date\":\"20220220 131519\",\"fromCurrency\": {\"code\":\"CHF\"},\"toCurrency\": {\"code\":\"EUR\"}, \"uid\":\"test-exchangeRate-uid-1234567890\"}").
	    post("/exchangeRate");
	}
	
	@Test
	@Order(4)
	public void testPutUpdateUser() {
		Response r = when().get("/exchangeRate?uid=test-exchangeRate-uid-1234567890").then().statusCode(200).extract().response();		
		Object id = r.path("id");
		Object mDate = r.path("version");

		// make sure the date was save without time component since it's declared with TemporalType.DATE
		Assert.assertEquals("20220220 000000", r.path("date"));
		Assert.assertEquals("CHF", r.path("fromCurrency.code"));
	
		// replace some values
		String body = r.asString().replace("20220220", "20220101").replace("CHF","GBP");
	
		// update the exchangeRate
		expect().statusCode(200).when().given().header("Content-Type", "application/json").body(body).put("/exchangeRate/" + id);

		// check update
		expect().statusCode(200).
    	body(
    			// check the values
    			"toCurrency.code", equalTo("EUR"),
    			"fromCurrency.code", equalTo("GBP"),
    			"rate",equalTo(1.234f),
    			"date",equalTo("20220101 000000"),
    			"version", not(equalTo(mDate))). 	// check the version has changed
  	    when().get("/exchangeRate/" + id);
  	      
	}	
	
	@Test
	@Order(5)
	public void testPatchUpdateUser() {
		Response r = when().get("/exchangeRate?uid=test-exchangeRate-uid-1234567890").then().statusCode(200).extract().response();		
		Object id = r.path("id");
		Object mDate = r.path("version");
		
		Assert.assertEquals("20220101 000000", r.path("date"));
		Assert.assertEquals("GBP", r.path("fromCurrency.code"));

		// patch the exchangeRate
		expect().statusCode(204).when().given().header("Content-Type", "application/json")
				.body("{\"rate\":2.345,\"fromCurrency\":{\"code\":\"USD\"}}").patch("/exchangeRate/"+id);

		// check
		expect().statusCode(200).
    	body(
    			// check the values
    			"toCurrency.code", equalTo("EUR"),
    			"fromCurrency.code", equalTo("USD"),
    			"rate",equalTo(2.345f),
    			"date",equalTo("20220101 000000"), 
    			"version", not(equalTo(mDate))). 	// check the version has changed
  	    when().get("/exchangeRate/" + id);
	}
	
	@Test
	@Order(6)
	public void testGetDeleteUser() {
		Object id = getIdForUid("exchangeRate", "test-exchangeRate-uid-1234567890");
		
		expect().statusCode(200).when().get("/exchangeRate/"+id);

		expect().statusCode(200).when().delete("/exchangeRate/"+id);
		
		expect().statusCode(404).when().get("/exchangeRate/"+id);
		
		expect().statusCode(404).when().get("/exchangeRate?uid=test-exchangeRate-uid-1234567890");
	}
	
}
