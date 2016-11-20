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
import w.wexpense.model.Payee;
import w.wexpense.service.StorableService;
import w.wexpense.test.config.DatabasePopulationConfig;
import w.wexpense.utils.PayeeUtils;

@Configuration
class PayeeServiceTestConfig extends DatabasePopulationConfig {
	public PayeeServiceTestConfig() {
		
		add(PayeeUtils.newPayee("one"));
		add(PayeeUtils.newPayee("two", add(new City("1000", "nowhere", add(new Country("CH","Swiss", add(new Currency("CHF", "Swiss Francs", 20))))))));
	};
}

@ContextConfiguration(classes = { PayeeServiceTestConfig.class })
public class PayeeServiceTest extends AbstractTest {
	
	@Autowired
	StorableService<Payee, Long> payeeService;
	
	@Test
	@Order(0)
	public void testSetup() {
		assertThat(payeeService).isNotNull();
		
		assertThat(payeeService.loadAll()).hasSize(2);
	}
	
	@Test
	@Order(1)
	public void testLoad() {
		Payee type = payeeService.save(PayeeUtils.newPayee("payee"));
		
		assertThat(type.getId()).isNotNull();
		assertThat(payeeService.loadAll()).hasSize(3);
		assertThat(payeeService.loadByUid(type.getUid())).isEqualTo(type);
	}
	
	@Test
	public void testNotLoadById() {
		assertThat(payeeService.load(-9L)).isNull();
	}
	
	@Test
	public void testNotLoadByUid() {
		assertThat(payeeService.loadByUid("xxxx")).isNull();
	}
}
