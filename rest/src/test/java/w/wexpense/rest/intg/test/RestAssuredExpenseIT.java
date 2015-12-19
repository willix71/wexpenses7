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

	private static final int expenseId = 61;
	private static final int expenseWithXRateId = 63;
	
	@Test
	public void testGetSingle() {
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
	public void testGetUnknown() {
	  expect().
	    statusCode(404). // default mapping is json
	    when().get("/expense/999999");
	}	
	
	@Test
	public void testGetWithExchangeRate() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    body(
	      "date", equalTo("20121202 000000"),
	      "amount",equalTo(86F),
	      "currency.code",equalTo("GBP"),
	      "allExchangeRates[0].rate",equalTo(1.48315F),
	      "transactions[0].value",equalTo(127.55F)). 
	    when().get("/expense/"+expenseWithXRateId);
	}
	
	@Test
	@Order(1)
	public void testGetAll() {
	  expect().
	    statusCode(200).
	    contentType("application/json"). 
	    body("uid", iterableWithSize(2)).
	    when().get("/expense");
	}
	
	@Test
	@Order(2)
	public void testGetPaged() {
	  expect().
	    statusCode(200).
	    contentType("application/json").
	    header("Link", not(containsString("next"))).
	    header("Link", not(containsString("last"))).
	    header("Link", not(containsString("prev"))).
	    header("Link", not(containsString("first"))).
	    body("uid", iterableWithSize(2)).
	    when().get("/expense?page=0&size=3&orderBy=date");
		  
	  expect().statusCode(404).when().get("/expense?page=1&size=3&orderBy=date");
	}
	
	@Test
	@Order(3)
	public void testCreate() throws IOException {	
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
	public void testPutUpdate() {
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
	public void testDelete() {
		Object id = getIdForUid("expense","test-expense-uid-1234567890");
		
		expect().statusCode(200).when().get("/expense/"+id);

		expect().statusCode(200).when().delete("/expense/"+id);
		
		expect().statusCode(404).when().get("/expense/"+id);
		
		expect().statusCode(404).when().get("/expense?uid=test-expense-uid-1234567890");
	}
	
	@Test
	@Order(10)
	public void testCreateForeignExpense() throws IOException {	
		String json = FileCopyUtils.copyToString(new FileReader(new File("src/test/resources/create_foreign_expense.json")));

		expect().
			statusCode(201).header("Location", startsWith(BASE_URI + "/expense/")).
			when().given().header("Content-Type", "application/json").body(json).post("/expense");
	  
		expect().statusCode(200).when().
			body(
		      "amount",equalTo(49.00F),
		      "currency.code",equalTo("GBP"),
		      "allExchangeRates[0].rate",equalTo(2F),
		      "transactions[0].amount",equalTo(49F),
		      "transactions[0].value",equalTo(100F)).
			get("/expense?uid=test-foreign-expense-uid-1234567890");
	}
	
	@Test
	@Order(11)
	public void testCleanUpCreateForeignExpense() throws IOException {	
		Response response = when().get("/expense?uid=test-foreign-expense-uid-1234567890").then().statusCode(200).extract().response();
		Object id = response.path("id");
		Object rateId = response.path("allExchangeRates[0].id");
		
		expect().statusCode(200).when().delete("/expense/"+id);
		expect().statusCode(200).when().delete("/exchangeRate/"+rateId);
	}
}
