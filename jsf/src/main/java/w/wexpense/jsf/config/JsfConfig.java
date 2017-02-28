package w.wexpense.jsf.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import figlet.FigletUtil;
import figlet.FigletUtil.Figlet;

@Configuration
@ImportResource({"classpath:jsf-context.xml","classpath:persistence-context.xml","classpath:service-context.xml"})
@PropertySource("classpath:wexpenses.properties")
public class JsfConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsfConfig.class);
	
	public JsfConfig() {
		LOGGER.info("\n" + FigletUtil.getMessage("Initiating JSF", Figlet.CHUNKY));
	}
}
