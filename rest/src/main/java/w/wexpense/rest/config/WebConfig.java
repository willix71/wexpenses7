package w.wexpense.rest.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Statement;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan({ "w.wexpense.rest", "w.expense.rest.events.listener" }) // TODO figure out why listeners are not included in the scan
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
		XStreamMarshaller xstreamMarshaller = new XStreamMarshaller();

		MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
		xmlConverter.setMarshaller(xstreamMarshaller);
		xmlConverter.setUnmarshaller(xstreamMarshaller);
		return xmlConverter;
	}

	// TODO this should be moved to test specific class
	
	@Autowired
	protected DataSource ds;

	@PostConstruct
	protected void populateDataBase() throws Exception {		
		InputStream is = this.getClass().getResourceAsStream(getPopulateFilename());
		if (is != null) {
			LOGGER.info("Populating database");
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
					Statement stament = ds.getConnection().createStatement()) {
				String line, sql = "";
				while ((line = reader.readLine()) != null) {
					if (line.startsWith("--"))
						continue;
					line = line.trim();
					if (line.isEmpty() || line.equals(";"))
						continue;
					sql += line;
					if (line.endsWith(";")) {
						stament.execute(sql);
						sql = "";
					}
				}
			}
		}
	}
	
	protected String getPopulateFilename() {
		String env = System.getProperty("wexpenses_env");
		return (env == null?"":"/" + env) + "/init-intg-db.sql";
	}
}