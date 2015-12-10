package w.wexpense.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;

import w.wexpense.AbstractTest;
import w.wexpense.model.PayeeType;
import w.wexpense.service.StorableService;
import w.wexpense.test.config.DatabasePopulationConfig;

import static org.assertj.core.api.Assertions.assertThat; 
import org.junit.Test;

@Configuration
class PayeeTypeServiceTestConfig extends DatabasePopulationConfig {
	public PayeeTypeServiceTestConfig() {
		add(new PayeeType("one", true));
		add(new PayeeType("two", false));
		add(new PayeeType("three", true));
	};
}

@ContextConfiguration(classes = { PayeeTypeServiceTestConfig.class })
public class PayeeTypeServiceTest extends AbstractTest {
	
	@Autowired
	StorableService<PayeeType, Long> payeeTypeService;
	
	@Test
	@Order(0)
	public void testSetup() {
		assertThat(payeeTypeService).isNotNull();
		
		assertThat(payeeTypeService.loadAll()).hasSize(3);
	}
	
	@Test
	@Order(1)
	public void testLoad() {
		PayeeType payee = payeeTypeService.save(new PayeeType("four", false));
		
		assertThat(payee.getId()).isNotNull();
		assertThat(payeeTypeService.loadAll()).hasSize(4);
		assertThat(payeeTypeService.loadByUid(payee.getUid())).isEqualTo(payee);
	}
	
	@Test
	public void testNotLoadById() {
		assertThat(payeeTypeService.load(-9L)).isNull();
	}
	
	@Test
	public void testNotLoadByUid() {
		assertThat(payeeTypeService.loadByUid("xxxx")).isNull();
	}
}
