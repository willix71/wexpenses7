package w.wexpense.rest.intg.test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static w.wexpense.rest.intg.test.ConfigTest.BASE_PATH;
import static w.wexpense.rest.intg.test.ConfigTest.BASE_SVR;
import static w.wexpense.rest.intg.test.ConfigTest.BASE_URI;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import w.junit.extras.OrderedJUnit4ClassRunner;

@RunWith(OrderedJUnit4ClassRunner.class)
public class RestAssuredCityIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestAssuredCityIT.class);
		
	@BeforeClass
	public static void setUp() {
		RestAssured.baseURI = BASE_SVR;
		RestAssured.basePath = BASE_PATH;
		
		LOGGER.info("baseURI and basePath set to " + ConfigTest.BASE_URI);
	}
	
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
	@Order(6)
	public void testGetDeleteUser() {
		// created at previous test
		Response response = 
				when().get("/city?uid=test-city-uid-1234567890").
				then().statusCode(200).extract().response();

		// extract id (it's an integer)
		Object id = response.path("id");
		
		expect().statusCode(200).when().get("/city/"+id);

		expect().statusCode(200).when().delete("/city/"+id);
		
		expect().statusCode(404).when().get("/city/"+id);
		
		expect().statusCode(404).when().get("/city?uid=test-city-uid-1234567890");
	}
	
}
