package w.wexpense.persistence;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import w.wexpense.test.config.TestPersistenceConfiguration;
import w.wexpense.test.config.TestServiceConfiguration;

public class DatabasePopulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePopulator.class);

	public static void main(String[] args) throws Exception {
		LOGGER.info("Application started by {} at {} ", System.getProperty("user.name"), new Date());

		String fileName = args.length==0?"script.sql":args[0];
		
		LOGGER.info("Writing to script to {} ", fileName);
		
		// Start the Spring container
		ApplicationContext context = new AnnotationConfigApplicationContext(
				TestPersistenceConfiguration.class, TestServiceConfiguration.class, DatabasePopulatorConfiguror.class);

		Connection connection = context.getBean(DataSource.class).getConnection();
		Statement stat = connection.createStatement();
		stat.execute("SCRIPT TO '" + fileName + "'");

		LOGGER.info("Done");
		
		System.exit(0);
	}
}
