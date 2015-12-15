package w.wexpense.persistence;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import w.wexpense.test.config.TestPersistenceConfiguration;
import w.wexpense.test.config.TestServiceConfiguration;

public class DatabasePopulationScriptWriter {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePopulationScriptWriter.class);

	public static void main(String[] args) throws Exception {
		LOGGER.info("Application started by {} at {} ", System.getProperty("user.name"), new Date());

		String fileName = args.length == 0 ? "script.sql" : args[0];

		LOGGER.info("Writing to script to {} ", fileName);

		// Start the Spring container
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				TestPersistenceConfiguration.class, TestServiceConfiguration.class,
				DefaultDatabasePopulationConfig.class);

		try (Connection connection = context.getBean(DataSource.class).getConnection()) {
			Statement stat = connection.createStatement();
			stat.execute("SCRIPT TO '" + fileName + "'");
		}

		// stop the spring context
		context.close();

		LOGGER.info("Done");

		System.exit(0);
	}
}
