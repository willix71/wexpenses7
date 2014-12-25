package w.wexpense.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import w.wexpense.validation.Ibanized;

@Entity
public class Payee extends DBable<Payee> {

	private static final long serialVersionUID = 2482940442245899869L;
	
    @ManyToOne(fetch = FetchType.EAGER)
    private PayeeType type;

    private String prefix;
    
    @NotEmpty
    private String name;

    private String address1;

    private String address2;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private City city;
    
    @Ibanized
    private String iban;
    
    @Pattern(regexp="|\\d{1,2}-\\d{1,6}-\\d")
    private String postalAccount;
    
    private String externalReference;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Payee bankDetails;
   
    private String display;
    
    @PrePersist
    @PreUpdate
    public void preupdate() {
    	display = toString().toLowerCase();
    	externalReference = buildExternalReference();
    }

	public String getDisplay() {
		return display;
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
	
	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public PayeeType getType() {
		return type;
	}

	public void setType(PayeeType type) {
		this.type = type;
	}

	public String getPrefixedName() {
		if (prefix == null) {
			return name;
		} else {
			return prefix + name;
		}
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String typeOf) {
		this.prefix = typeOf;
	}

	public String getExternalReference() {
		return externalReference;
	}
	
	public String buildExternalReference() {
		if (iban == null) {
			if (postalAccount == null) {
				return null;
			} else {
				return "CP:"+postalAccount;
			}
		} else {
			if (postalAccount == null) {
				return "IBAN:"+iban;
			} else {
				return "IBAN:"+iban + " | CP:"+postalAccount;
			}
		}	
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
	
	public Payee getBankDetails() {
		return bankDetails;
	}

	public void setBankDetails(Payee bankDetails) {
		this.bankDetails = bankDetails;
	}
	
	@Override
	public String toString() {
		String s = getPrefixedName();
		if (city != null) s += ", " + city.toString();	
		if (externalReference != null) s += "; " + externalReference;
		return s;
	}
}
