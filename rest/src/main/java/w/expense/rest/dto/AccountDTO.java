package w.expense.rest.dto;

import w.wexpense.model.enums.AccountEnum;

public class AccountDTO extends DBableDTO {
	private static final long serialVersionUID = 1L;

	//private List<Account> children;
	private DBableDTO parent;
	private String number;
	private String name;
	private String fullName;
	private String fullNumber;
	private AccountEnum type;
	private CodableDTO currency;
	private String externalReference;
	private DBableDTO owner;
	private boolean selectable;
	private String display;
	
	public DBableDTO getParent() {
		return parent;
	}
	public void setParent(DBableDTO parent) {
		this.parent = parent;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSelectable() {
		return selectable;
	}
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getFullNumber() {
		return fullNumber;
	}
	public void setFullNumber(String fullNumber) {
		this.fullNumber = fullNumber;
	}
	public AccountEnum getType() {
		return type;
	}
	public void setType(AccountEnum type) {
		this.type = type;
	}
	public CodableDTO getCurrency() {
		return currency;
	}
	public void setCurrency(CodableDTO currency) {
		this.currency = currency;
	}
	public String getExternalReference() {
		return externalReference;
	}
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}
	public DBableDTO getOwner() {
		return owner;
	}
	public void setOwner(DBableDTO owner) {
		this.owner = owner;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
}
