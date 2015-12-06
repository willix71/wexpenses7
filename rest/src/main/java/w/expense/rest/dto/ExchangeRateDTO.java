package w.expense.rest.dto;

import java.util.Date;

public class ExchangeRateDTO extends DBableDTO {
	private static final long serialVersionUID = 1L;

	private DBableDTO institution;
	private Date date;
	private CodableDTO toCurrency;
	private CodableDTO fromCurrency;
	private Double rate;
	private Double fee;
	private Double fixFee;
	
	public DBableDTO getInstitution() {
		return institution;
	}
	public void setInstitution(DBableDTO institution) {
		this.institution = institution;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public CodableDTO getToCurrency() {
		return toCurrency;
	}
	public void setToCurrency(CodableDTO toCurrency) {
		this.toCurrency = toCurrency;
	}
	public CodableDTO getFromCurrency() {
		return fromCurrency;
	}
	public void setFromCurrency(CodableDTO fromCurrency) {
		this.fromCurrency = fromCurrency;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Double getFee() {
		return fee;
	}
	public void setFee(Double fee) {
		this.fee = fee;
	}
	public Double getFixFee() {
		return fixFee;
	}
	public void setFixFee(Double fixFee) {
		this.fixFee = fixFee;
	}
}
