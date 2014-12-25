package w.wexpense.persistence;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceConfiguration.class);
	
	//@Value( "${jdbc.driverClassName}" ) 
	private String driverClassName;
	
	//@Value( "${jdbc.url}" ) 
	private String url;
	
	//@Value( "${jdbc.username}" ) 
	private String username;
	
	//@Value( "${jdbc.password}" ) 
	private String password;
		
	//@Value( "${jdbc.jpa.adapter.name}" ) 
	private String jpaAdapterName = "Hibernate";
	
	//@Value( "${jdbc.jpa.adapter.properties.extension}" ) 
	private String jpaAdapterPropertiesExtension = "";

	private Properties jpaAdapterProperties;
	
	@Bean
	public DataSource wxDataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setUrl(url);
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() throws Exception {	
		final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource( wxDataSource() );
		factoryBean.setPackagesToScan( new String[ ] { "w.wexpense.model" } );
		
		factoryBean.setJpaVendorAdapter( getJpaVendorAdapter() );
		factoryBean.setJpaProperties( getJpaAdapterProperties( ));

		return factoryBean;
	}
	
	@Bean
	public JpaTransactionManager transactionManager() throws Exception {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
		return transactionManager;
	}

//	@Bean
//	public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
//		return new PersistenceExceptionTranslationPostProcessor();
//	}
	
	protected JpaVendorAdapter getJpaVendorAdapter() throws Exception {
		String jpaAdapterClassName = "org.springframework.orm.jpa.vendor." + jpaAdapterName + "JpaVendorAdapter";

		LOGGER.debug("JpaVendorAdapter class name {}", jpaAdapterClassName);
		@SuppressWarnings("unchecked")
		Class <JpaVendorAdapter> clazz = (Class<JpaVendorAdapter>) Class.forName(jpaAdapterClassName);	
		return clazz.newInstance() ;
	}
	
	protected Properties getJpaAdapterProperties() throws IOException {
		if (jpaAdapterProperties == null) {
			
			String propertiesFilename = jpaAdapterName;
			if (jpaAdapterPropertiesExtension!=null) {
				propertiesFilename += jpaAdapterPropertiesExtension;
			}
			propertiesFilename += ".properties";
			
			LOGGER.debug("Loading JpaAdapterProperties from {}", propertiesFilename);
			
			Properties properties = new Properties();			
			properties.load(new ClassPathResource(propertiesFilename).getInputStream());
			
			jpaAdapterProperties = properties;
		}
		return jpaAdapterProperties;
	}

	public String getUrl() {
        return url;
    }
	
	public void setUrl(String url) {
		this.url = url;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

    public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setJpaAdapterName(String jpaAdapterName) {
		this.jpaAdapterName = jpaAdapterName;
	}

	public void setJpaAdapterPropertiesExtension(
			String jpaAdapterPropertiesExtension) {
		this.jpaAdapterPropertiesExtension = jpaAdapterPropertiesExtension;
	}

	public void setJpaAdapterProperties(Properties jpaAdapterProperties) {
		this.jpaAdapterProperties = jpaAdapterProperties;
	}
}
