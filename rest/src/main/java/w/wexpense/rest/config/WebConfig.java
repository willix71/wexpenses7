package w.wexpense.rest.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan({"w.wexpense.rest","w.expense.rest.events.listener"}) // TODO figure out why listeners are not included in the scan
public class WebConfig extends WebMvcConfigurerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);
	
	public WebConfig() {
		super();
		LOGGER.info("WebConfig initialized");
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(createXmlHttpMessageConverter());
		converters.add(new MappingJackson2HttpMessageConverter());
		
		super.configureMessageConverters(converters);
	}
	
	private HttpMessageConverter<Object> createXmlHttpMessageConverter() {
		XStreamMarshaller  xstreamMarshaller = new XStreamMarshaller();
		
		MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();	
		xmlConverter.setMarshaller(xstreamMarshaller);
		xmlConverter.setUnmarshaller(xstreamMarshaller);
		return xmlConverter;
	}
}