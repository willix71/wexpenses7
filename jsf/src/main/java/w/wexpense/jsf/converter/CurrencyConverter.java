package w.wexpense.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import w.wexpense.model.Currency;
import w.wexpense.service.StorableService;

//@FacesConverter("WexCurrencyConverter")
@Component("WexCurrencyConverter")
public class CurrencyConverter implements Converter {

	@Autowired
	private StorableService<Currency, String> currencyService;

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		return value==null || value.trim().length() == 0?null:currencyService.load(value);
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
		return object== null?null:String.valueOf(((Currency) object).getCode());
	}

}
