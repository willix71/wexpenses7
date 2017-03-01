package w.wexpense.jsf.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import w.utils.DateUtils;

@FacesConverter("WexDateConverter")
public class DateConverter implements Converter {

	private String format = "dd.MM.yyyy";
	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		return DateUtils.toDate(arg2);
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return  new SimpleDateFormat(format).format((Date) arg2);
	}

}
