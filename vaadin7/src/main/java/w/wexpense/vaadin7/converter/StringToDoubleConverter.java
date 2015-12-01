package w.wexpense.vaadin7.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import com.fathzer.soft.javaluator.DoubleEvaluator;
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
            return fullEval(value);
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
    
    private static final DoubleEvaluator EVALUATOR = new DoubleEvaluator();
    public static Double fullEval(String string) {
    	return EVALUATOR.evaluate(string);
    }
    
    public static Double simpleEval(String string) {
		if (string.startsWith("+")) string = string.substring(1);
		else if (string.startsWith("-")) string = "0" +string;
		else if (string.startsWith("/"))  string = "1" +string;
		
		StringTokenizer st = new StringTokenizer(string,"+-*/",true);
		Double c = new Double(st.nextToken());
		while(st.hasMoreTokens()) {
			String operator = st.nextToken();
			Double operande = new Double(st.nextToken());
			switch(operator.charAt(0)) {
			case '+':
				c += operande;
				break;
			case '-':
				c -= operande;
				break;
			case '*':
				c *= operande;
				break;
			case '/':				
				c /= operande;
				break;
			default:
				throw new IllegalArgumentException("Unknown operator " + operator);
			}
		}
		return c;
	}
}
