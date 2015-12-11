package w.wexpense.rest.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDTO extends DBableDTO {
	private static final long serialVersionUID = 1L;

	private String date;
	private BigDecimal amount;
	private CodableDTO currency;
	private DBableDTO payee;
	private String payed;
	private DBableDTO type;
	private String externalReference;
	private String description;
	private DBableDTO payment;
	private List<TransactionLineDTO> transactions = new ArrayList<>();
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public CodableDTO getCurrency() {
		return currency;
	}
	public void setCurrency(CodableDTO currency) {
		this.currency = currency;
	}
	public DBableDTO getPayee() {
		return payee;
	}
	public void setPayee(DBableDTO payee) {
		this.payee = payee;
	}
	public String getPayed() {
		return payed;
	}
	public void setPayed(String payed) {
		this.payed = payed;
	}
	public DBableDTO getType() {
		return type;
	}
	public void setType(DBableDTO type) {
		this.type = type;
	}
	public String getExternalReference() {
		return externalReference;
	}
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public DBableDTO getPayment() {
		return payment;
	}
	public void setPayment(DBableDTO payment) {
		this.payment = payment;
	}
	public List<TransactionLineDTO> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<TransactionLineDTO> transactions) {
		this.transactions = transactions;
	}
}