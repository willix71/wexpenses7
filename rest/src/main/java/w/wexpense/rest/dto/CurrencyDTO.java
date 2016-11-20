package w.wexpense.rest.dto;

public class CurrencyDTO extends CodableDTO {
	private static final long serialVersionUID = 1L;
	
	private Integer roundingFactor;	
	private Integer strengh;
	
	public CurrencyDTO() {
		super();
	}
	public CurrencyDTO(String code, String name) {
		super(code, name);
	}
	public CurrencyDTO(String code, String name, Integer roundingFactor, Integer strengh) {
		super(code, name);
		this.roundingFactor = roundingFactor;
		this.strengh = strengh;
	}
	
	public Integer getRoundingFactor() {
		return roundingFactor;
	}
	public void setRoundingFactor(Integer roundingFactor) {
		this.roundingFactor = roundingFactor;
	}
	public Integer getStrengh() {
		return strengh;
	}
	public void setStrengh(Integer strengh) {
		this.strengh = strengh;
	}
}
