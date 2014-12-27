package w.wexpense.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.NotEmpty;

import w.wexpense.model.enums.TransactionLineEnum;

@Entity
public class Template extends DBable<Template> {

	private static final long serialVersionUID = 2482940442245899869L;
	
	@NotEmpty
	private String templateName;
	
	private String templateDescription;
	
	private String templateMenu;
	
	private Integer templateOrder;
	
	private BigDecimal amount;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Currency currency;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Payee payee;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private ExpenseType type;
	
	private String externalReference;
	
	private String description;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Account outAccount;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Discriminator outDiscriminator;
	
	private TransactionLineEnum outFactor = TransactionLineEnum.OUT;
			
	@ManyToOne(fetch = FetchType.EAGER)
	private Account inAccount;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Discriminator inDiscriminator;
	
	private TransactionLineEnum inFactor = TransactionLineEnum.IN;
	
	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateDescription() {
		return templateDescription;
	}

	public void setTemplateDescription(String templateDescription) {
		this.templateDescription = templateDescription;
	}

	public String getTemplateMenu() {
		return templateMenu;
	}

	public void setTemplateMenu(String templateMenu) {
		this.templateMenu = templateMenu;
	}

	public Integer getTemplateOrder() {
		return templateOrder;
	}

	public void setTemplateOrder(Integer templateOrder) {
		this.templateOrder = templateOrder;
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

	public Account getOutAccount() {
		return outAccount;
	}
	
	public void setOutAccount(Account outAccount) {
		this.outAccount = outAccount;
	}
	
	public Discriminator getOutDiscriminator() {
		return outDiscriminator;
	}

	public void setOutDiscriminator(Discriminator discriminator) {
		this.outDiscriminator = discriminator;
	}
	
	public Account getInAccount() {
		return inAccount;
	}
	
	public void setInAccount(Account inAccount) {
		this.inAccount = inAccount;
	}
	
	public Discriminator getInDiscriminator() {
		return inDiscriminator;
	}

	public void setInDiscriminator(Discriminator discriminator) {
		this.inDiscriminator = discriminator;
	}
	
	public TransactionLineEnum getOutFactor() {
		return outFactor;
	}

	public void setOutFactor(TransactionLineEnum outFactor) {
		this.outFactor = outFactor;
	}

	public TransactionLineEnum getInFactor() {
		return inFactor;
	}

	public void setInFactor(TransactionLineEnum inFactor) {
		this.inFactor = inFactor;
	}

	@Override
	public String toString() {		
		return templateName;
	}
	
	@Override
   public Template duplicate() {
		Template klone = super.duplicate();
		klone.setTemplateName(null);
		return klone;
   }

	public Expense toExpense() {
		Expense x = new Expense();
		x.setType(type);
		x.setAmount(amount);
		x.setCurrency(currency);
		x.setPayee(payee);
		x.setExternalReference(externalReference);
		x.setDescription(description);
		x.setTransactions(new ArrayList<TransactionLine>());
		
		if (outAccount != null || inAccount != null) {
			TransactionLine tl = new TransactionLine();			
			tl.setAccount(outAccount);
			tl.setDiscriminator(outDiscriminator);
			tl.setFactor(outFactor);
			tl.setAmount(amount);
			tl.setValue(amount);
			x.addTransaction(tl);

			tl = new TransactionLine();
			tl.setAccount(inAccount);
			tl.setDiscriminator(inDiscriminator);
			tl.setFactor(inFactor);
			tl.setAmount(amount);
			tl.setValue(amount);
			x.addTransaction(tl);
		}
		return x;
	}
}
