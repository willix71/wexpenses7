package w.wexpense.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import w.wexpense.AbstractTest;
import w.wexpense.persistence.DefaultDatabasePopulationConfig;
import w.wexpense.service.model.IExpenseService;

@ContextConfiguration(classes = { DefaultDatabasePopulationConfig.class })

public class ExpenseServiceTest extends AbstractTest {

	@Autowired
	IExpenseService expenseService;
	
	@Test
	@Order(0)
	public void testSetup() {
		assertThat(expenseService).isNotNull();
		
		assertThat(expenseService.count()).isEqualTo(2);
	}
	
	@Test
	@Order(1)
	@Transactional
	public void testExchangeRate() {
		Expense x = expenseService.loadPage(0, 1, "-id").getContent().get(0);
		assertThat(x.getCurrency().getCode()).isEqualTo("GBP");
		
		// same exchange rate for both transaction lines
		assertThat(x.getTransactions().get(0).getExchangeRate()).isNotNull();
		assertThat(x.getTransactions().get(0).getExchangeRate()).isSameAs(x.getTransactions().get(1).getExchangeRate());
		
		// yet we have only one exchange rate for the expense rate
		assertThat(x.getAllExchangeRates().size()).isEqualTo(1);
	}
}
