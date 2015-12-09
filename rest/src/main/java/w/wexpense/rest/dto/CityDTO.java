package w.wexpense.rest.dto;

public class CityDTO extends DBableDTO {
	private static final long serialVersionUID = 1L;
	
	private String zip;
	private String name;
	private CodableDTO country;
	
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CodableDTO getCountry() {
		return country;
	}
	public void setCountry(CodableDTO country) {
		this.country = country;
	}
}
