package w.wexpense.rest.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ImportResource({"classpath:persistence-context.xml","classpath:service-context.xml"})
@PropertySource("classpath:wexpenses.properties")
public class ApplicationConfig {

	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}
}
