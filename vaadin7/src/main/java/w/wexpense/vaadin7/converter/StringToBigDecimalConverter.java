package w.wexpense.vaadin7.converter;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class StringToBigDecimalConverter implements Converter<String, BigDecimal> {

    private static final long serialVersionUID = 1L;

    @Override
    public BigDecimal convertToModel(String value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null || value.length() == 0)
            return null;
        else
            return new BigDecimal(value);
    }

    @Override
    public String convertToPresentation(BigDecimal value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null)
            return null;
        else
            return MessageFormat.format("{0,number,0.00}", value);
    }

    @Override
    public Class<BigDecimal> getModelType() {
        return BigDecimal.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
