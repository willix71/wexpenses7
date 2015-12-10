package w.wexpense.rest.intg.test;

import static w.wexpense.rest.intg.test.ConfigTest.BASE_URI;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import w.wexpense.rest.dto.CurrencyDTO;

public class CurrencyRestIT {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyRestIT.class);

	@BeforeClass
	public static void setUp() {
		LOGGER.info("baseURI set to " + ConfigTest.BASE_URI);
	}
	
	@Test
	public void testGetCurrency() {
		String URI = BASE_URI +"/currency/{id}";
		RestTemplate restTemplate = new RestTemplate();
		CurrencyDTO currency = restTemplate.getForObject(URI, CurrencyDTO.class, "CHF");
		Assert.assertEquals("CHF", currency.getCode());
		Assert.assertEquals("Swiss Francs", currency.getName());
	}
	
	@Test
	public void testGetCurrencies() {
		String URI = BASE_URI +"/currency";
		RestTemplate restTemplate = new RestTemplate();
		List<?> countries = restTemplate.getForObject(URI, List.class);
		Assert.assertEquals(4, countries.size());
	}
	
	@Test
	public void testGetCurrency_page1size3() {
		String URI = BASE_URI +"/currency?page=1&size=3";
		RestTemplate restTemplate = new RestTemplate();
		List<?> countries = restTemplate.getForObject(URI, List.class);
		Assert.assertEquals(1, countries.size());
	}
	
}
