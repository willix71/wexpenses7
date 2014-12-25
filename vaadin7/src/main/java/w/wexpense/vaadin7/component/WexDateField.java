package w.wexpense.vaadin7.component;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.utils.DateUtils;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;

public class WexDateField extends DateField {

	private static final long serialVersionUID = 8561115907318437297L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WexDateField.class);	

	public WexDateField() {
      setResolution(Resolution.DAY);
      setDateFormat("dd.MM.yyyy");
      setImmediate(true);
      setBuffered(false);
	}
	
	@Override
	protected Date handleUnparsableDateString(String dateString)
			throws Converter.ConversionException {

		return getEasyDate(dateString);
	}
	
	public static Date getEasyDate(String dateString) {
		try { 
			return DateUtils.toDate(dateString);
		}catch (Exception e) {
			LOGGER.debug("Can not parse date " + dateString);
			throw new Converter.ConversionException(e);
		}
	}
}
