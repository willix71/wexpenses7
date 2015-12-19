package w.wexpense.rest.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import w.utils.DateUtils;
import w.wexpense.model.Account;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.enums.AccountEnum;
import w.wexpense.rest.config.WebConfig;
import w.wexpense.utils.AccountUtils;
import w.wexpense.utils.ExchangeRateUtils;
import w.wexpense.utils.ExpenseUtils;
import w.wexpense.utils.PayeeUtils;

public class MappingTest {

	public ModelMapper getModelMapper() {
		return WebConfig.newModelMapper();
	}
	
	@Test
	public void whenConvertCountryEntityToDTO() {
		Country entity = new Country("CH", "Swiss", new Currency("CHF", "Swiss Francs", null));

		CountryDTO dto = getModelMapper().map(entity, CountryDTO.class);
		assertThat(dto.getCode()).isEqualTo("CH");
		assertThat(dto.getName()).isEqualTo("Swiss");
		assertThat(dto.getCurrency().getCode()).isEqualTo("CHF");
	}

	@Test
	public void whenConvertCountryDTOToEntity() {
		CountryDTO dto = new CountryDTO("CH", "Swiss", new CurrencyDTO("CHF", "Swiss Francs"));

		Country entity = getModelMapper().map(dto, Country.class);

		assertThat(entity.getCode()).isEqualTo("CH");
		assertThat(entity.getName()).isEqualTo("Swiss");
		assertThat(entity.getCurrency().getCode()).isEqualTo("CHF");
	}

	@Test
	public void whenConvertCountryDTOToExistingEntity() {
		CountryDTO dto = new CountryDTO("CH", null, new CurrencyDTO("CHF", "Swiss Francs"));
		Country entity = new Country("zz", "Swiss", null);

		getModelMapper().map(dto,entity);
		
		assertThat(entity.getCode()).isEqualTo("CH");
		assertThat(entity.getName()).isNull();
		assertThat(entity.getCurrency().getCode()).isEqualTo("CHF");
	}

	
	@Test
	public void whenConvertMapToExistingEntity() {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("code", "CH");
		
		Country entity = new Country("zz", "Swiss", null);
		
		getModelMapper().map(values, entity);

		assertThat(entity.getCode()).isEqualTo("CH");
		assertThat(entity.getName()).isEqualTo("Swiss");
		assertThat(entity.getCurrency()).isNull();
	}
	
	@Test
	public void whenConvertAccountEntityToDTO() {
		Currency chf = new Currency("CHF", "Swiss Francs", null);
		Account parent = new Account(null, 1, "asset", AccountEnum.ASSET, chf);
		Account entity = new Account(parent, 2, "cash", AccountEnum.ASSET, chf);

		ModelMapper modelMapper = getModelMapper();
		
		AccountDTO dto = modelMapper.map(entity, AccountDTO.class);
		assertThat(dto.getUid()).isEqualTo(entity.getUid());
		assertThat(dto.getNumber()).isEqualTo("2");
		assertThat(dto.getName()).isEqualTo("cash");
		assertThat(dto.getParent()).as("parent").isNotNull();
		assertThat(dto.getParent().getUid()).isEqualTo(parent.getUid());
	}

	@Test
	public void whenConvertAccountDTOToEntity() {
		AccountDTO dto = new AccountDTO();
		dto.setId(123L);
		dto.setNumber("2");
		dto.setName("cash");
		dto.setCurrency(new CodableDTO("CHF", "Swiss Francs"));
		dto.setParent(new DBableDTO(1L, 2L, "1234567890", "parent"));

		Account entity = getModelMapper().map(dto, Account.class);
		assertThat(entity.getId()).isEqualTo(123L);
		assertThat(entity.getName()).isEqualTo("cash");
		assertThat(entity.getParent()).isNotNull();
		assertThat(entity.getParent().getUid()).isEqualTo("1234567890");
	}
	
	@Test
	public void whenConvertExchangeRateEntityToDTO() {
		ExchangeRate entity = ExchangeRateUtils.newExchangeRate(new Currency("CHF",null,null), new Currency("EUR",null,null), 1.234, DateUtils.toDate(1,2,3004,11,55));

		ExchangeRateDTO dto = getModelMapper().map(entity, ExchangeRateDTO.class);
		assertThat(dto.getFromCurrency().getCode()).isEqualTo("CHF");
		assertThat(dto.getToCurrency().getCode()).isEqualTo("EUR");
		assertThat(dto.getRate()).isEqualTo(1.234);
		assertThat(dto.getDate()).isEqualTo("30040201 115500");
	}

	@Test
	public void whenConvertExpenseEntityToDTO() {
		Expense entity = ExpenseUtils.newExpense(110, new Currency("CHF",null,null), DateUtils.toDate(1,1,2001), PayeeUtils.newPayee("Migros"), AccountUtils.newAccount("cash"), AccountUtils.newAccount("groceries") );
		
		BigDecimal chf100 = new BigDecimal(110);
		ExpenseDTO dto = getModelMapper().map(entity, ExpenseDTO.class);
		assertThat(dto.getAmount()).isEqualTo(chf100);
		assertThat(dto.getDate()).isEqualTo("20010101 000000");
		assertThat(dto.getTransactions().size()).isEqualTo(2);
		
		assertThat(dto.getTransactions().size()).isEqualTo(2);
		assertThat(dto.getTransactions().get(0).getDate()).isEqualTo("20010101 000000");
		assertThat(dto.getTransactions().get(0).getAmount()).isEqualTo(chf100);
		assertThat(dto.getTransactions().get(0).getValue()).isEqualTo(chf100);
		
		assertThat(dto.getAllExchangeRates().size()).isEqualTo(0);
		
		ExchangeRate xrate = ExchangeRateUtils.newExchangeRate(new Currency("CHF",null,null), new Currency("EUR",null,null), 1.234, DateUtils.toDate(1,2,3004,11,55));
		entity.getTransactions().get(0).setExchangeRate(xrate);
		
		ExpenseDTO dto2 = getModelMapper().map(entity, ExpenseDTO.class);
		assertThat(dto2.getAllExchangeRates().size()).isEqualTo(1);
		
		entity.getTransactions().get(1).setExchangeRate(xrate);
		ExpenseDTO dto3 = getModelMapper().map(entity, ExpenseDTO.class);
		assertThat(dto3.getAllExchangeRates().size()).isEqualTo(1);
		
		ExchangeRate xrate2 = ExchangeRateUtils.newExchangeRate(new Currency("CHF",null,null), new Currency("EUR",null,null), 4.321, DateUtils.toDate(1,2,3004,23,55));
		entity.getTransactions().get(1).setExchangeRate(xrate2);
		ExpenseDTO dto4 = getModelMapper().map(entity, ExpenseDTO.class);
		assertThat(dto4.getAllExchangeRates().size()).isEqualTo(2);
		
	}
	
	@Test
	public void testResetAllExchangeRates() throws Exception {
		ExpenseDTO dto = new ObjectMapper().readValue(new File("src/test/resources/create_foreign_expense.json"), ExpenseDTO.class);
		
		// convert the dto to the entity
		Expense entity = getModelMapper().map(dto, Expense.class);
		entity.resetTransactions();
		
		assertThat(entity.getAllExchangeRates().size()).isEqualTo(1);
		ExchangeRate rate = entity.getAllExchangeRates().iterator().next();
		assertThat(rate.getFromCurrency().getCode()).isEqualTo("GBP");
		assertThat(rate.getToCurrency().getCode()).isEqualTo("CHF");		
		assertThat(rate.getRate()).isEqualTo(2);

		assertThat(entity.getTransactions().size()).isEqualTo(2);
		assertThat(entity.getTransactions().get(0).getExchangeRate()).isSameAs(rate);
		assertThat(entity.getTransactions().get(1).getExchangeRate()).isSameAs(rate);
	}
}
