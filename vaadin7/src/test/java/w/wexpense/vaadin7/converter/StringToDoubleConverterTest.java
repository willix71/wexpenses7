package w.wexpense.vaadin7.converter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StringToDoubleConverterTest {

	@Test
	public void testSimpleEvalNumber() {
		assertThat(Double.valueOf(2.0), equalTo(StringToDoubleConverter.simpleEval("2")));
		assertThat(Double.valueOf(2.1), equalTo(StringToDoubleConverter.simpleEval("2.1")));
		assertThat(Double.valueOf(-2.1), equalTo(StringToDoubleConverter.simpleEval("-2.1")));
		assertThat(Double.valueOf(2.1), equalTo(StringToDoubleConverter.simpleEval("+2.1")));
		assertThat(Double.valueOf(.5), equalTo(StringToDoubleConverter.simpleEval("/2")));
	}
	
	@Test
	public void testSimpleEvalOperation() {
		assertThat(Double.valueOf(4.0), equalTo(StringToDoubleConverter.simpleEval("2+2")));
		assertThat(Double.valueOf(0.0), equalTo(StringToDoubleConverter.simpleEval("2-2")));
		assertThat(Double.valueOf(10.0), equalTo(StringToDoubleConverter.simpleEval("2*5")));
		assertThat(Double.valueOf(3.0), equalTo(StringToDoubleConverter.simpleEval("9/3")));
	}
	
	@Test
	public void testSimpleEval() {
		assertThat(Double.valueOf(0.0), equalTo(StringToDoubleConverter.simpleEval("-2+2")));
		assertThat(Double.valueOf(1.0), equalTo(StringToDoubleConverter.simpleEval("2-2+2/2")));
	}
	
	@Test
	public void testFullEval() {
		assertThat(Double.valueOf(7.0), equalTo(StringToDoubleConverter.fullEval("2^3 - 3 + 1 + 3 * ((4+4*4)/2) / 5 + -5")));
	}
}
