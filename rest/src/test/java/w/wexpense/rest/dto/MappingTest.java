package w.wexpense.rest.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import w.wexpense.model.Account;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.enums.AccountEnum;

public class MappingTest {

	@Test
	public void whenConvertCountryEntityToDTO() {
		Country entity = new Country("CH", "Swiss", new Currency("CHF", "Swiss Francs", null));

		CountryDTO dto = new ModelMapper().map(entity, CountryDTO.class);
		assertThat(dto.getCode()).isEqualTo("CH");
		assertThat(dto.getName()).isEqualTo("Swiss");
		assertThat(dto.getCurrency().getCode()).isEqualTo("CHF");
	}

	@Test
	public void whenConvertCountryDTOToEntity() {
		CountryDTO dto = new CountryDTO("CH", "Swiss", new CurrencyDTO("CHF", "Swiss Francs"));

		Country entity = new ModelMapper().map(dto, Country.class);

		assertThat(entity.getCode()).isEqualTo("CH");
		assertThat(entity.getName()).isEqualTo("Swiss");
		assertThat(entity.getCurrency().getCode()).isEqualTo("CHF");
	}

	@Test
	public void whenConvertCountryDTOToExistingEntity() {
		CountryDTO dto = new CountryDTO("CH", null, new CurrencyDTO("CHF", "Swiss Francs"));
		Country entity = new Country("zz", "Swiss", null);

		new ModelMapper().map(dto,entity);
		
		assertThat(entity.getCode()).isEqualTo("CH");
		assertThat(entity.getName()).isNull();
		assertThat(entity.getCurrency().getCode()).isEqualTo("CHF");
	}

	
	@Test
	public void whenConvertMapToExistingEntity() {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("code", "CH");
		
		Country entity = new Country("zz", "Swiss", null);
		
		new ModelMapper().map(values, entity);

		assertThat(entity.getCode()).isEqualTo("CH");
		assertThat(entity.getName()).isEqualTo("Swiss");
		assertThat(entity.getCurrency()).isNull();
	}
	
	@Test
	@Ignore //TODO parent is not mapped correctly
	public void whenConvertAccountEntityToDTO() {
		Currency chf = new Currency("CHF", "Swiss Francs", null);
		Account parent = new Account(null, 1, "asset", AccountEnum.ASSET, chf);
		Account entity = new Account(parent, 2, "cash", AccountEnum.ASSET, chf);

		PropertyMap<Account, AccountDTO> parentMap = new PropertyMap<Account, AccountDTO>() {
			protected void configure() {
				try {
				map().setParent(new DBableDTO(source.getId(), source.getVersion(), source.getUid(), source.getDisplay()));
				} catch(Exception e) {
					throw e;
				}
			}
		};
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(parentMap);

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

		Account entity = new ModelMapper().map(dto, Account.class);
		assertThat(entity.getId()).isEqualTo(123L);
		assertThat(entity.getName()).isEqualTo("cash");
		assertThat(entity.getParent()).isNotNull();
		assertThat(entity.getParent().getUid()).isEqualTo("1234567890");
	}
}
