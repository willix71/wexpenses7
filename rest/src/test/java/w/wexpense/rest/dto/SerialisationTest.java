package w.wexpense.rest.dto;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import w.utils.DateUtils;

import static org.assertj.core.api.Assertions.assertThat;
public class SerialisationTest {

	@Test
	public void testDeserialisation() throws Exception {
		final String json = "{\"code\":\"CH\",\"name\":\"Swiss\",\"currency\":{\"code\":\"CHF\"}}";
		
		CountryDTO countryDto = new ObjectMapper().readValue(json , CountryDTO.class);
		
		assertThat(countryDto.getCode()).isEqualTo("CH");
		assertThat(countryDto.getName()).isEqualTo("Swiss");
		assertThat(countryDto.getCurrency().getCode()).isEqualTo("CHF");
	}
	
	@Test
	public void testSerialisation() throws Exception {
		final CountryDTO countryDto = new CountryDTO("CH","Swiss", new CodableDTO("CHF",null));
		
		String json = new ObjectMapper().writeValueAsString(countryDto); 
		
		assertThat(json).isEqualTo("{\"code\":\"CH\",\"name\":\"Swiss\",\"currency\":{\"code\":\"CHF\",\"name\":null}}");
		assertThat(countryDto.getName()).isEqualTo("Swiss");
		assertThat(countryDto.getCurrency().getCode()).isEqualTo("CHF");
	}
	
	@Test
	public void testDeserialisatioWithDate() throws Exception {
		final String json = "{\"rate\":1.234,\"toCurrency\":{\"code\":\"EUR\"},\"fromCurrency\":{\"code\":\"CHF\"},\"date\":\"2001-06-23\"}";
		
		ObjectMapper mapper = new ObjectMapper();
		
		ExchangeRateDTO xRateDto = mapper.readValue(json , ExchangeRateDTO.class);
		
		assertThat(xRateDto.getFromCurrency().getCode()).isEqualTo("CHF");
		assertThat(xRateDto.getToCurrency().getCode()).isEqualTo("EUR");
		assertThat(xRateDto.getRate()).isEqualTo(1.234);
		// TODO assertThat(xRateDto.getDate()).isEqualTo(DateUtils.toDate(23,6,2001));
	}
	
	@Test
	public void testSerialisationWithDate() throws Exception {
		final ExchangeRateDTO x = new ExchangeRateDTO();
		x.setToCurrency(new CodableDTO("EUR", null));
		x.setFromCurrency(new CodableDTO("CHF", null));
		x.setRate(1.234);
		// TODO x.setDate(DateUtils.toDate(2,3,2345,11,55));
		
		String json = new ObjectMapper().writeValueAsString(x); 
		
		assertThat(json).contains("\"rate\":1.234,");
		assertThat(json).contains("\"toCurrency\":{\"code\":\"EUR\"");
		assertThat(json).contains("\"fromCurrency\":{\"code\":\"CHF\"");
		System.out.println(json);
	}
}
