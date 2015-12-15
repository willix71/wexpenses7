package w.wexpense.rest.intg.test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static w.wexpense.rest.intg.test.ConfigTest.BASE_URI;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.annotation.Order;
import org.springframework.util.FileCopyUtils;

import com.jayway.restassured.response.Response;

/**
 *  This test validates Dbable entities using a EntityMgrDaoService
 *  
 * @author willix
 *
 */
public class RestAssuredExpenseIT extends AbstractRestAssured {

	private static final int expenseId = 59;
	
	@Test
	public void testGetSingleUser() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    body(
	      "date", equalTo("20150201 000000"),
	      "currency.code",equalTo("CHF")).
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
	    body("uid", iterableWithSize(1)).
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
	    body("uid", iterableWithSize(1)).
	    when().get("/expense?page=0&size=3&orderBy=date");
		  
	  expect().statusCode(404).when().get("/expense?page=1&size=3&orderBy=date");
	}
	
	@Test
	@Order(3)
	public void testGetCreateUser() throws IOException {	
		String json = FileCopyUtils.copyToString(new FileReader(new File("src/test/resources/create_expense.json")));

	  expect().
	    statusCode(201).
	    header("Location", startsWith(BASE_URI + "/expense/")).
	    when().given().
	    header("Content-Type", "application/json").
	    body(json).
	    post("/expense");
	}
	
	@Test
	@Order(4)
	public void testPutUpdateUser() {
		Response r = when().get("/expense?uid=test-expense-uid-1234567890").then().statusCode(200).extract().response();		
		Object id = r.path("id");
		Object mDate = r.path("version");

		Number d = r.path("amount");
		Assert.assertEquals(80.0f,d.floatValue(),0.00001);
	
		// replace the selectable value to true
		String body = r.asString().replace("80.00", "122.00");
	
		// update the type
		expect().statusCode(200).when().given().header("Content-Type", "application/json").body(body).put("/expense/" + id);

		expect().statusCode(200).
    	body(
  	      "amount",equalTo(122.00f), 		// check the new selectable value
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
