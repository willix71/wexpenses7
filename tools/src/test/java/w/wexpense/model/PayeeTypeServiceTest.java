package w.wexpense.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;

import w.wexpense.AbstractTest;
import w.wexpense.model.PayeeType;
import w.wexpense.service.StorableService;
import w.wexpense.test.config.DatabasePopulationConfig;
import w.wexpense.test.utils.PersistenceHelper;

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
	
	@Autowired
	private PersistenceHelper persistenceHelper;
	
	@Test
	@Order(0)
	public void testSetup() {
		assertThat(payeeTypeService).isNotNull();
		
		assertThat(payeeTypeService.loadAll()).hasSize(3);
	}
	
	@Test
	@Order(1)
	public void testLoad() {
		PayeeType type = payeeTypeService.save(new PayeeType("four", false));
		
		assertThat(type.getId()).isNotNull();
		assertThat(payeeTypeService.loadAll()).hasSize(4);
		assertThat(payeeTypeService.loadByUid(type.getUid())).isEqualTo(type);
		
		assertThat(persistenceHelper.getVersion(PayeeType.class, type.getId())).isEqualTo(type.getVersion());
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
