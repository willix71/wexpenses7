package w.wexpense.rest.dto;

public class PayeeDTO extends DBableDTO {
	private static final long serialVersionUID = 1L;
	
    private DBableDTO type;
    private String prefix;
    private String name;
    private String address1;
    private String address2;
    private DBableDTO city;
    private String iban;    
    private String postalAccount;       
    private DBableDTO bankDetails;
    private String externalReference;
    
	public DBableDTO getType() {
		return type;
	}
	public void setType(DBableDTO type) {
		this.type = type;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public DBableDTO getCity() {
		return city;
	}
	public void setCity(DBableDTO city) {
		this.city = city;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public String getPostalAccount() {
		return postalAccount;
	}
	public void setPostalAccount(String postalAccount) {
		this.postalAccount = postalAccount;
	}
	public DBableDTO getBankDetails() {
		return bankDetails;
	}
	public void setBankDetails(DBableDTO bankDetails) {
		this.bankDetails = bankDetails;
	}
	public String getExternalReference() {
		return externalReference;
	}
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	} 
}
