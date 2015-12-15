package w.wexpense.rest.dto;

import java.math.BigDecimal;

import w.wexpense.model.enums.TransactionLineEnum;

public class TransactionLineDTO extends DBableDTO {
	private static final long serialVersionUID = 1L;

	private DBableDTO expense;
	//private AccountPeriod period;
	private DBableDTO discriminator;
	private DBableDTO account;
	private TransactionLineEnum factor = TransactionLineEnum.OUT;
	private BigDecimal amount;
	private DBableDTO exchangeRate;
	private BigDecimal value;
	private BigDecimal balance;
	private String date;
	private DBableDTO consolidation;
	private String description;
	
	public DBableDTO getExpense() {
		return expense;
	}
	public void setExpense(DBableDTO expense) {
		this.expense = expense;
	}
//	public AccountPeriod getPeriod() {
//		return period;
//	}
//	public void setPeriod(AccountPeriod period) {
//		this.period = period;
//	}
	public DBableDTO getDiscriminator() {
		return discriminator;
	}
	public void setDiscriminator(DBableDTO discriminator) {
		this.discriminator = discriminator;
	}
	public DBableDTO getAccount() {
		return account;
	}
	public void setAccount(DBableDTO account) {
		this.account = account;
	}
	public TransactionLineEnum getFactor() {
		return factor;
	}
	public void setFactor(TransactionLineEnum factor) {
		this.factor = factor;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public DBableDTO getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(DBableDTO exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public DBableDTO getConsolidation() {
		return consolidation;
	}
	public void setConsolidation(DBableDTO consolidation) {
		this.consolidation = consolidation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
