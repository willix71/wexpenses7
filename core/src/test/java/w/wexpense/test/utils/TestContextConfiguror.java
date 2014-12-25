package w.wexpense.test.utils;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ImportResource("classpath:TestContextConfiguror-context.xml")
public class TestContextConfiguror implements ApplicationContextAware {

	private EmbeddedDataSourceConfig dataSourceConfig;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// hack using @ActiveProfiles annotation to tell this configuror which additional SQL scripts to run
		// or which individual INSERT statements to execute prior to running the tests

		this.dataSourceConfig = new EmbeddedDataSourceConfig();
		StringBuilder inserts = new StringBuilder();
		for(String profile: applicationContext.getEnvironment().getActiveProfiles()) {
			if (profile.endsWith(".sql")) {
				this.dataSourceConfig.setScripts("classpath:" + profile);
			} else if (profile.toUpperCase().startsWith("INSERT INTO")) {
				inserts.append(profile).append("\n");
			}
		}
		
		if (inserts.length()>0) {
			// add individual INSERT statements
			this.dataSourceConfig.setStatments(inserts.toString());
		}
	}

	@Bean
	public DataSource dataSource() {
		return this.dataSourceConfig.getDataSource();
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setPackagesToScan("w.wexpense.model");
		factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factory.setJpaDialect(new HibernateJpaDialect());
		factory.setJpaProperties(getJpaProperties());
		return factory;
	}
	
	@Bean
	public JpaTransactionManager transactionManager() {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
		return transactionManager;
	}
	
	protected Properties getJpaProperties() {
		Properties p = new Properties();
		p.put("hibernate.dialect","org.hibernate.dialect.H2Dialect");
		p.put("hibernate.hbm2ddl.auto","create");
		p.put("hibernate.show_sql","true");
		p.put("hibernate.cache.use_query_cache","false");
		p.put("hibernate.cache.use_second_level_cache","false");
		return p;
		
	}
}
