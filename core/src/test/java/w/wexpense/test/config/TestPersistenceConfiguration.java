package w.wexpense.test.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import w.wexpense.persistence.PersistenceConfiguration;
import w.wexpense.test.utils.PersistenceHelper;

@Configuration
@EnableTransactionManagement
public class TestPersistenceConfiguration extends PersistenceConfiguration {
   
	@Bean
	public PersistenceHelper persistenceHelper() {
		return new PersistenceHelper();
	}
	
   @Bean
   @Override
   public DataSource wxDataSource() {
      EmbeddedDatabaseFactory dbFactory = new EmbeddedDatabaseFactory();
      dbFactory.setDatabaseType(EmbeddedDatabaseType.H2);
      return dbFactory.getDatabase();
   }

   @Override
   protected Properties getJpaAdapterProperties() {
      Properties p = new Properties();
      p.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
      p.put("hibernate.hbm2ddl.auto", "create");
      p.put("hibernate.show_sql", "true");
      p.put("hibernate.cache.use_query_cache", "false");
      p.put("hibernate.cache.use_second_level_cache", "false");
      return p;
   }

}
