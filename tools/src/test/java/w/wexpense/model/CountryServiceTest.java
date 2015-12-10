package w.wexpense.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;

import w.wexpense.AbstractTest;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.service.StorableService;
import w.wexpense.test.config.DatabasePopulationConfig;

@Configuration
class CountryServiceTestConfig extends DatabasePopulationConfig {
	public CountryServiceTestConfig() {
		add(new Country("CH","Swiss", add(new Currency("CHF", "Swiss Francs", 20))));
		add(new Country("F","France", add(new Currency("EUR", "Euro", 100))));
	};
}

@ContextConfiguration(classes = { CountryServiceTestConfig.class })
public class CountryServiceTest extends AbstractTest {
	
	@Autowired
	StorableService<Currency, String> currencyService;
	
	@Autowired
	StorableService<Country, String> countryService;
	
	@Test
	@Order(0)
	public void testSetup() {
		assertThat(currencyService).isNotNull();
		assertThat(countryService).isNotNull();
		
		assertThat(countryService.loadAll()).hasSize(2);
	}
	
	@Test
	@Order(1)
	public void testLoad() {
		Country c = countryService.save(new Country("XZ", "test country", currencyService.load("CHF")));
		
		assertThat(countryService.loadAll()).hasSize(3);
		assertThat(countryService.load("XZ")).isEqualTo(c);
	}
	
	@Test
	public void testNotLoadById() {
		assertThat(countryService.load("xx")).isNull();
	}
	
	@Test
	public void testNotLoadByUid() {
		assertThat(countryService.loadByUid("xxxx")).isNull();
	}
}
