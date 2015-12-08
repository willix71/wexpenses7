package w.wexpense.rest.intg.test;

import static w.wexpense.rest.intg.test.ConfigTest.BASE_URI;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import w.expense.rest.dto.CountryDTO;

public class CountryRestIT {

	@Test
	public void testGetCountry() {
		String URI = BASE_URI +"/country/{id}";
		RestTemplate restTemplate = new RestTemplate();
		CountryDTO country = restTemplate.getForObject(URI, CountryDTO.class, "CH");
		Assert.assertEquals("CH", country.getCode());
		Assert.assertEquals("Switzerland", country.getName());
	}
	
	@Test
	public void testGetCountries() {
		String URI = BASE_URI +"/country";
		RestTemplate restTemplate = new RestTemplate();
		List<?> countries = restTemplate.getForObject(URI, List.class);
		Assert.assertEquals(6, countries.size());
	}
	
	@Test
	public void testGetCountry_page1size3() {
		String URI = BASE_URI +"/country?page=1&size=3";
		RestTemplate restTemplate = new RestTemplate();
		List<?> countries = restTemplate.getForObject(URI, List.class);
		Assert.assertEquals(3, countries.size());
	}
	
}
