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
	
	private final String date_time_format = "dd.MM.yyyy HH:mm:ss";

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		return arg2==null||arg2.trim().length() == 0?null:DateUtils.toDate(arg2);
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if (arg2 == null) return null;
		Date d = (Date) arg2;
		String f = new SimpleDateFormat(date_time_format).format(d);
		if (f.endsWith(" 00:00:00")) return f.substring(0, f.length()-9);
		if (f.endsWith(":00")) return f.substring(0, f.length()-3);
		return f;	
	}

}
