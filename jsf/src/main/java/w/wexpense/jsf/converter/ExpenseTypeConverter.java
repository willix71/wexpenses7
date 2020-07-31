package w.wexpense.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import w.wexpense.jsf.config.ServiceLocator;
import w.wexpense.model.ExpenseType;
import w.wexpense.service.StorableService;

@FacesConverter(value = "WexExpenseTypeConverter")
public class ExpenseTypeConverter implements Converter {

	private StorableService<ExpenseType, String> currencyService = ServiceLocator.getService("expenseTypeService", StorableService.class);

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		return value==null || value.trim().length() == 0?null:currencyService.loadByName(value);
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
		return object== null?null:String.valueOf(((ExpenseType) object).getName());
	}

}
