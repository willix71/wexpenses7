package w.wexpense.test.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import w.wexpense.persistence.PersistenceConfiguration;

@Configuration
@EnableTransactionManagement
public class TestIntgPersistenceConfiguration extends PersistenceConfiguration {

	private DataSource singleDataSource;

	private synchronized DataSource getSingleDataSource() throws IOException {
		if (singleDataSource == null) {
			EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder()					
					.setName("Intg" + System.currentTimeMillis()) // needed or else the mem is called several times
					.setType(EmbeddedDatabaseType.H2);

			Properties p = super.getJpaAdapterProperties();
			String script = p.getProperty("hibernate.hbm2ddl.import_files");
			if (script != null) {
				String autoP = p.getProperty("hibernate.hbm2ddl.auto");
				if (autoP == null || !autoP.startsWith("create")) {
					builder.addScript(script).build();
				}
			}
			singleDataSource = builder.build();
		}
		return singleDataSource;
	}

	@Bean
	@Override
	public DataSource wxDataSource() throws IOException {
		return getSingleDataSource();
	}
}