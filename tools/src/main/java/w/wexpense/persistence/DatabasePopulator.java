package w.wexpense.persistence;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import w.utils.DateUtils;
import w.wexpense.dta.BvoDtaFormater;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.PayeeType;
import w.wexpense.test.config.TestDatabaseConfiguror;
import w.wexpense.test.config.TestServiceConfiguror;
import w.wexpense.test.populator.TestDatabasePopulator;
import w.wexpense.test.utils.PersistenceHelper;
import w.wexpense.utils.AccountUtils;
import w.wexpense.utils.ExchangeRateUtils;
import w.wexpense.utils.PayeeUtils;

@Configuration
class DatabasePopulatorConfiguror extends TestDatabasePopulator {
	public DatabasePopulatorConfiguror() {
		Currency chf = add(new Currency("CHF", "Swiss Francs", 20));
		Currency euro = add(new Currency("EUR", "Euro", 100));
		Currency usd = add(new Currency("USD", "US Dollar", 100));
		Currency gbp = add(new Currency("GBP", "British Pounds", 100));

		Country ch = add(new Country("CH", "Switzerland", chf));
		Country f = add(new Country("FR", "France", euro));
		add(new Country("IT", "Italie", euro));
		add(new Country("DE", "Germany", euro));
		add(new Country("US", "United States of America", usd));

		add(new City(null, "Paris", f));
		add(new City("1260", "Nyon", ch));
		add(new City("1197", "Prangins", ch));
		
		add(ExchangeRateUtils.newExchangeRate(euro,chf,DateUtils.getDate(1,1,2000),1.6));
		add(ExchangeRateUtils.newExchangeRate(euro,chf,DateUtils.getDate(),1.2));
		add(ExchangeRateUtils.newExchangeRate(gbp,chf,DateUtils.getDate(),1.48315));
	};
}

public class DatabasePopulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePopulator.class);

	public static void main(String[] args) throws Exception {
		LOGGER.info("Application started by {} at {} ", System.getProperty("user.name"), new Date());

		String fileName = args.length==0?"script.sql":args[0];
		
		LOGGER.info("Writing to script to {} ", fileName);
		
		// Start the Spring container
		ApplicationContext context = new AnnotationConfigApplicationContext(
				TestDatabaseConfiguror.class, TestServiceConfiguror.class, DatabasePopulatorConfiguror.class);

		Connection connection = context.getBean(DataSource.class).getConnection();
		Statement stat = connection.createStatement();
		stat.execute("SCRIPT TO '" + fileName + "'");

		LOGGER.info("Done");
	}
}
