package w.wexpense.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;

import w.wexpense.AbstractTest;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.service.StorableService;
import w.wexpense.test.config.DatabasePopulationConfig;

@Configuration
class CityServiceTestConfig extends DatabasePopulationConfig {
	public CityServiceTestConfig() {
		Country ch = add(new Country("CH","Swiss", add(new Currency("CHF", "Swiss Francs", 20))));
		add(new City("1000", "mille", ch));
		add(new City("100", "cent", ch));
	};
}

@ContextConfiguration(classes = { CityServiceTestConfig.class })
public class CityServiceTest extends AbstractTest {
	
	@Autowired
	StorableService<Country, String> countryService;
	
	@Autowired
	StorableService<City, Long> cityService;
	
	@Test
	@Order(0)
	public void testSetup() {
		assertThat(cityService).isNotNull();
		
		assertThat(cityService.loadAll()).hasSize(2);
	}
	
	@Test
	@Order(1)
	public void testLoad() {
		City type = cityService.save(new City("10","dix", countryService.load("CH")));
		
		assertThat(type.getId()).isNotNull();
		assertThat(cityService.loadAll()).hasSize(3);
		assertThat(cityService.loadByUid(type.getUid())).isEqualTo(type);
	}
	
	@Test
	public void testNotLoadById() {
		assertThat(cityService.load(-9L)).isNull();
	}
	
	@Test
	public void testNotLoadByUid() {
		assertThat(cityService.loadByUid("xxxx")).isNull();
	}
}
