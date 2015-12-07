package w.wexpense.test.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import w.wexpense.test.utils.PersistenceHelper;

@Configuration
@EnableTransactionManagement
public class TestDatabaseConfiguror  {
   
	@Bean
	public PersistenceHelper persistenceHelper() {
		return new PersistenceHelper();
	}
	
   @Bean
   public DataSource dataSource() {
      EmbeddedDatabaseFactory dbFactory = new EmbeddedDatabaseFactory();
      dbFactory.setDatabaseType(EmbeddedDatabaseType.H2);
      return dbFactory.getDatabase();
   }

   @Bean
   public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
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
      transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
      return transactionManager;
   }

   protected Properties getJpaProperties() {
      Properties p = new Properties();
      p.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
      p.put("hibernate.hbm2ddl.auto", "create");
      p.put("hibernate.show_sql", "true");
      p.put("hibernate.cache.use_query_cache", "false");
      p.put("hibernate.cache.use_second_level_cache", "false");
      return p;
   }

}
