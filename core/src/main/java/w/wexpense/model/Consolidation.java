package w.wexpense.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import w.wexpense.validation.Warning;

@Entity
public class Consolidation extends DBable<Consolidation> {

	private static final long serialVersionUID = 2482940442245899869L;
	
	@NotNull
	@ManyToOne
	private Payee institution;

	@NotNull
	@Temporal(TemporalType.DATE)
	private Date date;
	
	private BigDecimal openingBalance;
	
	private BigDecimal closingBalance;
	
	@Range(min=0,max=0, message="Delta balance should be 0", groups=Warning.class)
	private BigDecimal deltaBalance;
	
    @OneToMany(mappedBy="consolidation", cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("date")
    private List<TransactionLine> transactions;

	public Payee getInstitution() {
		return institution;
	}

	public void setInstitution(Payee institution) {
		this.institution = institution;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(BigDecimal openingBalance) {
		this.openingBalance = openingBalance;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public BigDecimal getDeltaBalance() {
		return deltaBalance;
	}

	public void updateDeltaBalance(BigDecimal balance) {
		BigDecimal delta = closingBalance == null ? BigDecimal.ZERO : closingBalance;
		if (openingBalance != null) {
			delta = delta.subtract(openingBalance);
		}
		if (balance != null) {
			delta = delta.subtract(balance);
		}

		this.deltaBalance = delta;
	}
	
	public List<TransactionLine> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<TransactionLine> transactions) {
		this.transactions = transactions;
	}
    
	public List<TransactionLine> resetTransaction() {
		List<TransactionLine> xs = this.transactions;
		this.transactions = new ArrayList<TransactionLine>();
		return xs;
	}
	
	@Override
	public String toString() {		
		return MessageFormat.format("{0,date,dd/MM/yyyy} {1}", date, institution);
	}
	
	@Override
   public Consolidation duplicate() {
		Consolidation klone = super.duplicate();
		klone.setTransactions(new ArrayList<TransactionLine>());
		return klone;
   }

	@Override
   public Consolidation klone() {
		Consolidation klone = super.klone();
		if (klone.getTransactions()!=null) {
			klone.setTransactions(new ArrayList<TransactionLine>(klone.getTransactions()));
		}
		return klone;
   }
}
