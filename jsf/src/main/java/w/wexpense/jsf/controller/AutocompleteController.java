package w.wexpense.jsf.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import w.wexpense.jsf.config.ServiceLocator;
import w.wexpense.model.Currency;
import w.wexpense.model.ExpenseType;
import w.wexpense.service.StorableService;

@ManagedBean(name = "autocompleteController")
@SessionScoped
public class AutocompleteController {

	private List<Currency> currencies;
	
	private List<ExpenseType> expenseTypes;

	@PostConstruct
	public void init() {
		currencies = ServiceLocator.getService("currencyService", StorableService.class).loadAll();
		expenseTypes = ServiceLocator.getService("expenseTypeService", StorableService.class).loadAll();
	}
	
	public List<Currency> autoCompleteCurrency(String code) {
		if (code==null || code.trim().length()==0) {
			return currencies;
		} else {
			String prefix=code.toUpperCase();
			return currencies.stream().filter(c->c.getCode().startsWith(prefix)).collect(Collectors.toList());
		}
	}
	
	public List<ExpenseType> autoCompleteExpenseType(String code) {
		if (code==null || code.trim().length()==0) {
			return expenseTypes;
		} else {
			String prefix=code.toUpperCase();
			return expenseTypes.stream().filter(c->c.getName().toUpperCase().startsWith(prefix)).collect(Collectors.toList());
		}
	}
	
	
	
}
