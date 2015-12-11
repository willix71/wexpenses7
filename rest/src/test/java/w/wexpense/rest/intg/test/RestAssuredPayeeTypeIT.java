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

public class RestAssuredPayeeTypeIT extends AbstractRestAssured {

	private static final int phonePayeeTypeId = 31;
	
	@Test
	public void testGetSingleUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    body(
	      "name", equalTo("Phone"),
	      "selectable",equalTo(true)).
	    when().get("/payeeType/"+phonePayeeTypeId);
	}
	
	@Test
	public void testGetSingleUserByUid() {
		
		Response response = 
				when().get("/payeeType/"+phonePayeeTypeId).
				then().statusCode(200).extract().response();
		
		String uid = response.path("uid"); // extract uid
		
		expect().
	    statusCode(200).
	    body("id", equalTo(phonePayeeTypeId)).
	    when().get("/payeeType?uid="+ uid);
	}

	@Test
	public void testGetUnknownUser() {
	  expect().
	    statusCode(404). // default mapping is json
	    when().get("/payeeType/999999");
	}
	
	@Test
	@Order(1)
	public void testGetAllUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json"). 
	    body("name", iterableWithSize(2)).
	    when().get("/payeeType");
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
	    body("code", iterableWithSize(2)).
	    when().get("/payeeType?page=0&size=3&orderBy=name");
		  
	  expect().statusCode(404).when().get("/payeeType?page=1&size=3&orderBy=name");
	}
	
	@Test
	@Order(3)
	public void testGetCreateUser() {
	  expect().
	    statusCode(201).
	    header("Location", startsWith(BASE_URI + "/payeeType/")).
	    when().given().
	    header("Content-Type", "application/json").
	    body("{\"name\":\"TestPayeeType\",\"selectable\":false, \"uid\":\"test-payeeType-uid-1234567890\"}").
	    post("/payeeType");
	}
	
	@Test
	@Order(4)
	public void testPutUpdateUser() {
		Response r = when().get("/payeeType?uid=test-payeeType-uid-1234567890").then().statusCode(200).extract().response();		
		Object id = r.path("id");
		Object mDate = r.path("version");

		// make sure the type is NOT selectable (see previous method)
		Assert.assertEquals(false, r.path("selectable"));
	
		// replace the selectable value to true
		String body = r.asString().replace("false", "true");
	
		// update the type
		expect().statusCode(200).when().given().header("Content-Type", "application/json").body(body).put("/payeeType/" + id);

		expect().statusCode(200).
    	body(
  	      "selectable",equalTo(true), 		// check the new selectable value
  	      "version", not(equalTo(mDate))). 	// check the version has changed
  	    when().get("/payeeType/" + id);
  	      
	}
	
	@Test
	@Order(6)
	public void testGetDeleteUser() {
		Object id = getIdForUid("payeeType","test-payeeType-uid-1234567890");
		
		expect().statusCode(200).when().get("/payeeType/"+id);

		expect().statusCode(200).when().delete("/payeeType/"+id);
		
		expect().statusCode(404).when().get("/payeeType/"+id);
		
		expect().statusCode(404).when().get("/payeeType?uid=test-payeeType-uid-1234567890");
	}
	
}
