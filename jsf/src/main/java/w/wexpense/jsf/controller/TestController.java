package w.wexpense.jsf.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import w.wexpense.model.Currency;
import w.wexpense.service.StorableService;

//@ManagedBean(name = "testController")
@Component("jsfTestController")
@RequestScoped
//@Scope("request")
public class TestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);
	
	private Date simpleDate,simpleDate2;
	private String simpleTime = null;
		
	@Autowired
	private StorableService<Currency, String> currencyService;
	
	private Currency currency;
	
	
//	public List<String> getCurrencies(String criteria) {
//		List<String> s = new ArrayList<>();
//		for(Currency c: currencyService.loadAll()) {
//			if (criteria == null || c.getName().startsWith(criteria)) {
//				s.add(c.getName());
//			}
//		}
//		return s;
//	}
	public List<Currency> getCurrencies(String criteria) {
		List<Currency> s = new ArrayList<>();
		for(Currency c: currencyService.loadAll()) {
			if (criteria == null || c.getName().startsWith(criteria)) {
				s.add(c);
			}
		}
		return s;
	}
//	public Map<String, String> getCurrencies(String criteria) {
//		Map<String,String> s = new HashMap<>();
//		for(Currency c: currencyService.loadAll()) {
//			if (criteria == null || c.getName().startsWith(criteria)) {
//				s.put(c.getCode(), c.getName());
//			}
//		}
//		return s;
//	}
	
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		LOGGER.warn("\n\tCurrency set {}", currency);
		this.currency = currency;
	}

	public Date getSimpleDate() {
		return simpleDate;
	}

	public void setSimpleDate(Date simpleDate) throws ParseException {
		this.simpleDate = simpleDate;
		computeSimpleDate();
	}
	
	public String getSimpleTime() {
		return simpleTime;
	}

	public void setSimpleTime(String simpleTime) throws ParseException {
		this.simpleTime = simpleTime;
		computeSimpleDate();
	}
	
	public void computeSimpleDate() throws ParseException {
		LOGGER.warn("\n\tNew date {}", simpleDate);
		if (simpleTime != null) {
			String h = new SimpleDateFormat("dd/MM/yyyy/").format(simpleDate) + simpleTime;
			this.simpleDate = new SimpleDateFormat("dd/MM/yyyy/HH:mm").parse(h);
		}
		
	}
	
	public Date getSimpleDate2() {
		return simpleDate2;
	}

	public void setSimpleDate2(Date simpleDate2) {
		LOGGER.warn("\n\tNew date2 {}", simpleDate2);
		this.simpleDate2 = simpleDate2;
	}
		
	
    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }
   
    public void click() {
        RequestContext requestContext = RequestContext.getCurrentInstance();         
        requestContext.update("form:display");
        requestContext.execute("PF('dlg').show()");
    }
    
	public String doit() {        
		return null;
	}
}
