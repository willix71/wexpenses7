package w.wexpense.vaadin7.converter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class StringToBigDecimalConverterTest {
	
	@Test
	public void testFullEval() {
		assertThat(new BigDecimal(7), equalTo(StringToBigDecimalConverter.eval("2^3 - 3 + 1 + 3 * ((4+4*4)/2) / 5 + -5")));
	}
}
