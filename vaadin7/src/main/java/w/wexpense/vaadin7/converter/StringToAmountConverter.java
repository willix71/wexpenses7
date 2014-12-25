package w.wexpense.vaadin7.converter;

import java.text.MessageFormat;
import java.util.Locale;

import w.wexpense.model.AmountValue;

import com.vaadin.data.util.converter.Converter;

public class StringToAmountConverter implements Converter<String, AmountValue> {

    private static final long serialVersionUID = 1L;

    @Override
    public AmountValue convertToModel(String value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null || value.length() == 0)
            return null;
        else
            return new AmountValue(value);
    }

    @Override
    public String convertToPresentation(AmountValue value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null)
            return null;
        else
            return MessageFormat.format("{0,number,0.00}", value.getRealValue());
    }

    @Override
    public Class<AmountValue> getModelType() {
        return AmountValue.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
