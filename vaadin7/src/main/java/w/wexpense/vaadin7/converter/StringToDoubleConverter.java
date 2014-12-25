package w.wexpense.vaadin7.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class StringToDoubleConverter implements Converter<String, Double> {

    private static final long serialVersionUID = 1L;

    private NumberFormat nf;

    public StringToDoubleConverter() {
        this("0.00");
    }

    public StringToDoubleConverter(String numberFormat) {
        this.nf = new DecimalFormat(numberFormat);
    }

    @Override
    public Double convertToModel(String value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null || value.length() == 0)
            return null;
        else
            return new Double(value);
    }

    @Override
    public synchronized String convertToPresentation(Double value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null)
            return null;
        else
            return nf.format(value);
    }

    @Override
    public Class<Double> getModelType() {
        return Double.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
