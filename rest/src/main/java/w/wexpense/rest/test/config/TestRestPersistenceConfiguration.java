package w.wexpense.rest.test.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Statement;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaDialect;
//import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import w.wexpense.persistence.PersistenceConfiguration;
import w.wexpense.test.config.TestPersistenceConfiguration;
import w.wexpense.test.utils.PersistenceHelper;

@Configuration
public class TestRestPersistenceConfiguration extends PersistenceConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestRestPersistenceConfiguration.class);

	// @Bean
	// public DataSource wxDataSource() throws Exception {
	// final DriverManagerDataSource dataSource = (DriverManagerDataSource)
	// Class.forName(driverManagerClassName).newInstance();
	// dataSource.setDriverClassName(driverClassName);
	// dataSource.setUsername(username);
	// dataSource.setPassword(password);
	// dataSource.setUrl(url);
	//
	// if (dataSource instanceof SingleConnectionDataSource) {
	// ((SingleConnectionDataSource) dataSource).setSuppressClose(true);
	// }
	//
	// return dataSource;
	// }

	// TODO this should be moved to test specific class

	// @Autowired
	// protected DataSource ds;

	@Bean
	@Override
	public DataSource wxDataSource() {
		EmbeddedDatabaseFactory dbFactory = new EmbeddedDatabaseFactory();
		dbFactory.setDatabaseType(EmbeddedDatabaseType.H2);
		InputStream is = this.getClass().getResourceAsStream(getPopulateFilename());
		if (is != null) {
			dbFactory.setDatabaseName("DB" + System.currentTimeMillis());
			DataSource ds = dbFactory.getDatabase();
			populateDataBase(ds, is);
			return ds;
		} else {
			return dbFactory.getDatabase();
		}

	}

	protected void populateDataBase(DataSource ds, InputStream is) throws RuntimeException {
		LOGGER.info("Populating database");
		try {
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
		} catch (Exception e) {
			throw new RuntimeException("Could not populate the database", e);
		}
	}

	protected String getPopulateFilename() {
		String env = System.getProperty("wexpenses_env");
		return (env == null ? "" : "/" + env) + "/init-intg-db.sql";
	}

}
