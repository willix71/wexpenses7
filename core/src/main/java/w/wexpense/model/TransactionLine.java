package w.wexpense.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.validation.ExchangeRateTransactionLineized;

@Entity
@TypeDefs({
	@TypeDef(name = "amountValueType", typeClass = w.wexpense.persistence.type.AmountValueType.class),
	@TypeDef(name = "accountPeriodType", typeClass = w.wexpense.persistence.type.AccountPeriodType.class),
	@TypeDef(name = "transactionLineEnumType", typeClass = w.wexpense.persistence.type.TransactionLineEnumType.class)
	})
@ExchangeRateTransactionLineized
public class TransactionLine extends DBable<TransactionLine> {

	private static final long serialVersionUID = 2482940442245899869L;
		
	@Type(type="accountPeriodType")
	private AccountPeriod period;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Expense expense;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Discriminator discriminator;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Account account;
	
	@NotNull
	@Type(type="transactionLineEnumType")
	private TransactionLineEnum factor = TransactionLineEnum.OUT;
	
	@NotNull
	private BigDecimal amount;
	    
	@ManyToOne(fetch = FetchType.EAGER)
	private ExchangeRate exchangeRate;
	
	@NotNull
	private BigDecimal value;
	
	private BigDecimal balance;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	@ManyToOne
	private Consolidation consolidation;
	   
	private String description;
	
	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	// TODO Remove once figured out a way to get Nested properties out of a BeanItemContainer
	public Payee getPayee() {
		return expense==null?null:expense.getPayee();
	}
	
	public Discriminator getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(Discriminator discriminator) {
		this.discriminator = discriminator;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public ExchangeRate getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(ExchangeRate exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	/**
	 * automatically calculates the amountValue based on the amount, 
	 * the exchange rate and the currency rounding factor
	 */
	public void updateValue() {
		// using double because it will be rounded by the AmountValue object anyway
		// so we don't need the precision of a BigDecimal
		
		if (amount == null) {
			value = null;
			return;
		}
		BigDecimal v = amount;
		
		Currency currency = null;
		if (exchangeRate != null) {
			v = v.multiply(BigDecimal.valueOf(exchangeRate.getRate()));			
			if (exchangeRate.getFee() != null) {
				
				v = v.multiply(BigDecimal.valueOf(1+exchangeRate.getFee()));
			}
			// get the currency of the exchange rate
			currency = exchangeRate.getToCurrency();
		}
		if (currency == null && account != null) {
			// fall back on the currency of the account
			currency = account.getCurrency();
		}
		if (currency != null && currency.getRoundingFactor() != null) {
			// perform rounding
			double round = Math.rint(v.doubleValue() * currency.getRoundingFactor());
			value = BigDecimal.valueOf(round).divide(BigDecimal.valueOf(currency.getRoundingFactor()));
		} else {
			value = v;
		}
	}

	@Override
	public void preupdate() {
		super.preupdate();
	}
	
	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getInValue() {
		return TransactionLineEnum.IN==factor?getValue():null;
	}

	public BigDecimal getOutValue() {
		return TransactionLineEnum.OUT==factor?getValue():null;
	}
	
	public TransactionLineEnum getFactor() {
		return factor;
	}

	public void setFactor(TransactionLineEnum factor) {
		this.factor = factor;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Consolidation getConsolidation() {
		return consolidation;
	}

	public void setConsolidation(Consolidation consolidation) {
		this.consolidation = consolidation;
	}

	public AccountPeriod getPeriod() {
		return period;
	}

	public void setPeriod(AccountPeriod period) {
		this.period = period;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		String s = account == null ? " " : account.toString();
		if (discriminator != null) {
			s += "/" + discriminator.toString();
		}
		return MessageFormat.format("{0} {1} {2,number, 0.00}", s , factor, amount );
	}
	
	@Override
   public TransactionLine duplicate() {
		TransactionLine klone = super.duplicate();
		klone.setConsolidation(null);
		return klone;
   }
	
	public boolean validate() {		
		if (exchangeRate != null && account.getCurrency() != null) {
			if (!account.getCurrency().equals(exchangeRate.getToCurrency())) {
				throw new ValidationException("transaction line's account and exchange rate currencies don't match");
			}
		}
		return true;
	}
}
