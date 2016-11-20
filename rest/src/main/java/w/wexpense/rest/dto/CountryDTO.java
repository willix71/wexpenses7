package w.wexpense.rest.dto;

public class CountryDTO extends CodableDTO {
	private static final long serialVersionUID = 1L;
	
	private CodableDTO currency;

	public CountryDTO() {
		super();
	}

	public CountryDTO(String code, String name, CodableDTO currency) {
		super(code, name);
		this.currency = currency;
	}

	public CodableDTO getCurrency() {
		return currency;
	}

	public void setCurrency(CodableDTO currency) {
		this.currency = currency;
	}
}
