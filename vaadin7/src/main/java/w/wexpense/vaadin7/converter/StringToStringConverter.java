package w.wexpense.vaadin7.converter;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class StringToStringConverter implements Converter<String, String> {

    private static final long serialVersionUID = 1L;

    @Override
    public String convertToModel(String value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value != null && value.length() == 0)
            return null;
        else
            return value;
    }

    @Override
    public String convertToPresentation(String value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        return value;
    }

    @Override
    public Class<String> getModelType() {
        return String.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
