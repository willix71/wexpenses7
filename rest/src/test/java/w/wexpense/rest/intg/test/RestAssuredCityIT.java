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
 *  This test validates Dbable entities using a JapRepoDaoService
 *  
 * @author willix
 *
 */
public class RestAssuredCityIT extends AbstractRestAssured {

	@Test
	public void testGetSingleUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    body(
	      "name", equalTo("London"),
	      "country.code",equalTo("UK")).
	    when().get("/city/1");
	}
	
	@Test
	public void testGetSingleUserByUid() {
		
		Response response = 
				when().get("/city/1").
				then().statusCode(200).extract().response();
		
		String uid = response.path("uid"); // extract uid
		
		expect().
	    statusCode(200).
	    body("id", equalTo(1)).
	    when().get("/city?uid="+ uid);
	}

	@Test
	public void testGetUnknownUser() {
	  expect().
	    statusCode(404). // default mapping is json
	    when().get("/city/999999");
	}
	
	@Test
	@Order(1)
	public void testGetAllUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json"). 
	    body("name", iterableWithSize(8)).
	    when().get("/city");
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
	    body("code", iterableWithSize(3)).
	    when().get("/city?page=0&size=3&orderBy=name");
	  
	  expect().
	    statusCode(200).
	    header("Link", containsString("next")).
	    header("Link", containsString("last")).
	    header("Link", containsString("prev")).
	    header("Link", containsString("first")).
	    body("code", iterableWithSize(3)).
	    when().get("/city?page=1&size=3&orderBy=name");
	  
	  expect().
	    statusCode(200).
	    header("Link", not(containsString("next"))).
	    header("Link", not(containsString("last"))).
	    header("Link", containsString("prev")).
	    header("Link", containsString("first")).
	    body("code", iterableWithSize(2)).
	    when().get("/city?page=2&size=3&orderBy=name");
	  
	  expect().statusCode(404).when().get("/city?page=3&size=3&orderBy=name");
	}
	
	@Test
	@Order(3)
	public void testGetCreateUser() {
	  expect().
	    statusCode(201).
	    header("Location", startsWith(BASE_URI + "/city/")).
	    when().given().
	    header("Content-Type", "application/json").
	    body("{\"name\":\"TestCity\",\"zip\":\"1000\",\"country\": {\"code\":\"CH\"}, \"uid\":\"test-city-uid-1234567890\"}").
	    post("/city");
	}
	
	@Test
	@Order(4)
	public void testPutUpdateUser() {
		Response r = when().get("/city?uid=test-city-uid-1234567890").then().statusCode(200).extract().response();		
		Object id = r.path("id");
		Object mDate = r.path("version");

		// make sure the zip is what we expect
		Assert.assertEquals("1000", r.path("zip"));
		Assert.assertEquals("CH", r.path("country.code"));
	
		// replace some values
		String body = r.asString().replace("1000", "1001").replace("CH","UK");
	
		// update the city
		expect().statusCode(200).when().given().header("Content-Type", "application/json").body(body).put("/city/" + id);

		// check update
		expect().statusCode(200).
    	body(
    			// check the values
    			"name",equalTo("TestCity"),	
    			"zip",equalTo("1001"), 			
    			"country.code",equalTo("UK"),
    			"country.name",equalTo("United Kingdom"), 
    			"version", not(equalTo(mDate))). 	// check the version has changed
  	    when().get("/city/" + id);
  	      
	}	
	
	@Test
	@Order(5)
	public void testPatchUpdateUser() {
		Response r = when().get("/city?uid=test-city-uid-1234567890").then().statusCode(200).extract().response();		
		Object id = r.path("id");
		Object mDate = r.path("version");
		
		// make sure the zip is what we expect
		Assert.assertEquals("1001", r.path("zip"));
		Assert.assertEquals("UK", r.path("country.code"));

		// patch the city
		expect().statusCode(204).when().given().header("Content-Type", "application/json")
				.body("{\"name\":\"PatchedCity\",\"country\":{\"code\":\"DE\"}}").patch("/city/"+id);

		// check
		expect().statusCode(200).
    	body(
    			// check the values
    			"name",equalTo("PatchedCity"),	
    			"zip",equalTo("1001"), 			
    			"country.code",equalTo("DE"),
    			"country.name",equalTo("Germany"), 
    			"version", not(equalTo(mDate))). 	// check the version has changed
  	    when().get("/city/" + id);
	}
	
	@Test
	@Order(6)
	public void testGetDeleteUser() {
		Object id = getIdForUid("city", "test-city-uid-1234567890");
		
		expect().statusCode(200).when().get("/city/"+id);

		expect().statusCode(200).when().delete("/city/"+id);
		
		expect().statusCode(404).when().get("/city/"+id);
		
		expect().statusCode(404).when().get("/city?uid=test-city-uid-1234567890");
	}
	
}
