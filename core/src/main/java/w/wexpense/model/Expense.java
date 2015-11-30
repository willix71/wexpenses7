package w.wexpense.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import w.wexpense.validation.Dtanized;
import w.wexpense.validation.Transactionized;
import w.wexpense.validation.Warning;

@Entity
@Dtanized(groups=Warning.class)
public class Expense extends DBable<Expense> {

	private static final long serialVersionUID = 2482940442245899869L;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	@NotNull
	private BigDecimal amount;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Currency currency;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name="PAYEE_OID")
	private Payee payee;
	
	private String payed;
	
	@ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name="TYPE_OID")
	private ExpenseType type;
	
	private String externalReference;
	
	private String description;
	
	@ManyToOne
	private Payment payment;
	
   @OneToMany(mappedBy="expense", cascade={CascadeType.ALL}, orphanRemoval=true)
   @OrderBy("factor, amount")
   @OnDelete(action=OnDeleteAction.CASCADE)
   @Transactionized
   @Size(min=2,message="A expense must have at least 2 transaction lines: one IN and one OUT")
   @Valid
   private List<TransactionLine> transactions = new ArrayList<>();
   
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Payee getPayee() {
		return payee;
	}

	public void setPayee(Payee payee) {
		this.payee = payee;
	}

	public ExpenseType getType() {
		return type;
	}

	public void setType(ExpenseType type) {
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
	
	public List<TransactionLine> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionLine> transactions) {
		this.transactions = transactions;
	}

	public void addTransaction(TransactionLine transaction) {
		if (transactions == null) {
			transactions = new ArrayList<TransactionLine>();
		}
		transactions.add(transaction);
		transaction.setExpense(this);
	}
	
   public void removeTransaction(TransactionLine transaction) {
      if (this.transactions != null) {
         if (this.transactions.remove(transaction)) {
            transaction.setExpense(null);
         }
      }
   }
	  
	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getPayed() {
		return payed;
	}

	public void setPayed(String payed) {
		this.payed = payed;
	}

	@Override
	public String toString() {		
		return MessageFormat.format("{0,date,dd/MM/yyyy} {1} {2,number, 0.00}{3}", date, payee, amount, currency);
	}
	
	@Override
   public Expense duplicate() {
		Expense klone = super.duplicate();
		kloneLines(klone, true);
		return klone;
   }

	@Override
   public Expense klone() {
		Expense klone = super.klone();
      if (getPayment()!=null && !getPayment().isSelectable()) {
         klone.setPayment(null);
      }
      kloneLines(klone, false);
		return klone;
   }
	
	private void kloneLines(Expense klone, boolean duplicate) {
		if (getTransactions() != null) {
			List<TransactionLine> newLines = new ArrayList<TransactionLine>();
			for (TransactionLine oldLine : getTransactions()) {
				TransactionLine newLine = duplicate ? oldLine.duplicate() : oldLine.klone();
				newLine.setExpense(klone);
				newLines.add(newLine);
			}
			klone.setTransactions(newLines);
		}
	}
}
