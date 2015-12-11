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
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.annotation.Order;

import com.jayway.restassured.response.Response;

/**
 *  This test validates Dbable entities using a EntityMgrDaoService
 *  
 * @author willix
 *
 */
@Ignore
public class RestAssuredExpenseIT extends AbstractRestAssured {

	private static final int expenseId = 59;
	
	@Test
	public void testGetSingleUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    body(
	      "name", equalTo("Phone"),
	      "selectable",equalTo(true)).
	    when().get("/expense/"+expenseId);
	}
	
	@Test
	public void testGetSingleUserByUid() {
		
		Response response = 
				when().get("/expense/"+expenseId).
				then().statusCode(200).extract().response();
		
		String uid = response.path("uid"); // extract uid
		
		expect().
	    statusCode(200).
	    body("id", equalTo(expenseId)).
	    when().get("/expense?uid="+ uid);
	}

	@Test
	public void testGetUnknownUser() {
	  expect().
	    statusCode(404). // default mapping is json
	    when().get("/expense/999999");
	}
	
	@Test
	@Order(1)
	public void testGetAllUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json"). 
	    body("name", iterableWithSize(2)).
	    when().get("/expense");
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
	    when().get("/expense?page=0&size=3&orderBy=name");
		  
	  expect().statusCode(404).when().get("/expense?page=1&size=3&orderBy=name");
	}
	
	@Test
	@Order(3)
	public void testGetCreateUser() {
	  expect().
	    statusCode(201).
	    header("Location", startsWith(BASE_URI + "/expense/")).
	    when().given().
	    header("Content-Type", "application/json").
	    body("{\"name\":\"TestPayeeType\",\"selectable\":false, \"uid\":\"test-expense-uid-1234567890\"}").
	    post("/expense");
	}
	
	@Test
	@Order(4)
	public void testPutUpdateUser() {
		Response r = when().get("/expense?uid=test-expense-uid-1234567890").then().statusCode(200).extract().response();		
		Object id = r.path("id");
		Object mDate = r.path("version");

		// make sure the type is NOT selectable (see previous method)
		Assert.assertEquals(false, r.path("selectable"));
	
		// replace the selectable value to true
		String body = r.asString().replace("false", "true");
	
		// update the type
		expect().statusCode(200).when().given().header("Content-Type", "application/json").body(body).put("/expense/" + id);

		expect().statusCode(200).
    	body(
  	      "selectable",equalTo(true), 		// check the new selectable value
  	      "version", not(equalTo(mDate))). 	// check the version has changed
  	    when().get("/expense/" + id);
  	      
	}
	
	@Test
	@Order(6)
	public void testGetDeleteUser() {
		Object id = getIdForUid("expense","test-expense-uid-1234567890");
		
		expect().statusCode(200).when().get("/expense/"+id);

		expect().statusCode(200).when().delete("/expense/"+id);
		
		expect().statusCode(404).when().get("/expense/"+id);
		
		expect().statusCode(404).when().get("/expense?uid=test-expense-uid-1234567890");
	}
	
}
