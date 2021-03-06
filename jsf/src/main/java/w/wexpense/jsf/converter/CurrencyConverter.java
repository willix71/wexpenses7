package w.wexpense.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import w.wexpense.jsf.config.ServiceLocator;
import w.wexpense.model.Currency;
import w.wexpense.service.StorableService;

@FacesConverter(value = "WexCurrencyConverter")
public class CurrencyConverter implements Converter {

	private StorableService<Currency, String> currencyService = ServiceLocator.getService("currencyService", StorableService.class);

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		return value==null || value.trim().length() == 0?null:currencyService.load(value);
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
		return object== null?null:String.valueOf(((Currency) object).getCode());
	}

}
