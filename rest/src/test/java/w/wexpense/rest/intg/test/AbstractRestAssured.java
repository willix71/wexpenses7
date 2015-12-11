package w.wexpense.rest.intg.test;

import static com.jayway.restassured.RestAssured.when;
import static w.wexpense.rest.intg.test.ConfigTest.BASE_PATH;
import static w.wexpense.rest.intg.test.ConfigTest.BASE_SVR;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import w.junit.extras.OrderedJUnit4ClassRunner;

@RunWith(OrderedJUnit4ClassRunner.class)
public abstract class AbstractRestAssured {

	@BeforeClass
	public static void setUp() {
		RestAssured.baseURI = BASE_SVR;
		RestAssured.basePath = BASE_PATH;

	}

	public Object getIdForUid(String type, String uid) {
		String url = "/" + type + "?uid=" + uid;

		// created at previous test
		Response response = when().get(url).then().statusCode(200).extract().response();

		// extract id (it's an integer)
		Object id = response.path("id");

		return id;
	}
}
