package w.wexpense.rest.config;

import java.util.List;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import w.wexpense.model.Account;
import w.wexpense.model.Discriminator;
import w.wexpense.rest.dto.DBableDTO;

@Configuration
@EnableWebMvc
@ComponentScan({ "w.wexpense.rest"})
public class WebConfig extends WebMvcConfigurerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);
	
	public WebConfig() {
		super();
		LOGGER.info("WebConfig initialized");
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new MappingJackson2HttpMessageConverter());
		converters.add(createXmlHttpMessageConverter());

		super.configureMessageConverters(converters);
	}

	private HttpMessageConverter<Object> createXmlHttpMessageConverter() {
		XStreamMarshaller xstreamMarshaller = new XStreamMarshaller();

		MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
		xmlConverter.setMarshaller(xstreamMarshaller);
		xmlConverter.setUnmarshaller(xstreamMarshaller);
		return xmlConverter;
	}

	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper =  new ModelMapper();
		modelMapper.addConverter(new AbstractConverter<Account, DBableDTO>() {
			@Override
			protected DBableDTO convert(Account source) {
				if (source == null) return null;
				DBableDTO dto =  new DBableDTO(source.getId(), source.getVersion(), source.getUid(), source.toString());
				return dto;
			}
		});
		modelMapper.addConverter(new AbstractConverter<Discriminator, DBableDTO>() {
			@Override
			protected DBableDTO convert(Discriminator source) {
				if (source == null) return null;
				DBableDTO dto =  new DBableDTO(source.getId(), source.getVersion(), source.getUid(), source.toString());
				return dto;
			}
		});
		return modelMapper;
	}
}