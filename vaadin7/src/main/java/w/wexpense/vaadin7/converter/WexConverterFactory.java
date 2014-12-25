package w.wexpense.vaadin7.converter;

import java.math.BigDecimal;
import java.util.Date;

import w.wexpense.model.AmountValue;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

public class WexConverterFactory extends DefaultConverterFactory {

    private static final long serialVersionUID = 1L;

    @Override
    protected Converter<String, ?> createStringConverter(Class<?> sourceType) {
        if (BigDecimal.class.isAssignableFrom(sourceType)) {
            return new StringToBigDecimalConverter();
        } else if (AmountValue.class.isAssignableFrom(sourceType)) {
            return new StringToAmountConverter();
        } else if (Date.class.isAssignableFrom(sourceType)) {
            return new StringToDateConverter();
        } else if (String.class.isAssignableFrom(sourceType)) {
            return new StringToStringConverter();
        } else {
            return super.createStringConverter(sourceType);
        }
    };

}
