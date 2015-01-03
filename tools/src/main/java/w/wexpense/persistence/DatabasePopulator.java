package w.wexpense.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Payee;
import w.wexpense.model.PayeeType;
import w.wexpense.test.utils.PersistenceHelper;
import w.wexpense.test.utils.TestDatabasePopulator;

@Configuration
public class DatabasePopulator extends TestDatabasePopulator {
   private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePopulator.class);

   @Override
   public List<Object> getPopulation() {
      List<Object> population = new ArrayList<Object>();
      Currency chf = new Currency("CHF", "Swiss Francs", 20);
      population.add(chf);
      Currency euro = new Currency("EUR", "Euro", 100);
      population.add(euro);
      Currency usd = new Currency("USD", "US Dollar", 100);
      population.add(usd);
      Currency gbp = new Currency("GBP", "British Pounds", 100);
      population.add(gbp);

      Country ch = new Country("CH", "Switzerland", chf);
      population.add(ch);
      Country f = new Country("FR", "France", euro);
      population.add(f);
      population.add(new Country("IT", "Italie", euro));
      population.add(new Country("DE", "Germany", euro));
      population.add(new Country("US", "United States of America", usd));

      City paris = new City(null, "Paris", f);
      population.add(paris);
      City nyon = new City("1260", "Nyon", ch);
      population.add(nyon);
      City prangins = new City("1197", "Prangins", ch);
      population.add(prangins);

      // === Exchange rates ===
      ExchangeRate euroToChf = new ExchangeRate();
      euroToChf.setDate(new GregorianCalendar(2000, 1, 1).getTime());
      euroToChf.setToCurrency(chf);
      euroToChf.setFromCurrency(euro);
      euroToChf.setRate(1.6);
      population.add(euroToChf);

      ExchangeRate gbpToChf = new ExchangeRate();
      gbpToChf.setDate(new Date());
      gbpToChf.setToCurrency(chf);
      gbpToChf.setFromCurrency(gbp);
      gbpToChf.setRate(1.48315);
      population.add(gbpToChf);

      euroToChf = new ExchangeRate();
      euroToChf.setDate(new Date());
      euroToChf.setToCurrency(chf);
      euroToChf.setFromCurrency(euro);
      euroToChf.setRate(1.2);
      population.add(euroToChf);
      
      return population;
   }

   private Payee newPayee(PayeeType type, String prefix, String name, String adr1, String adr2, City city, String xRef) {
      Payee payee = new Payee();
      payee.setType(type);
      payee.setPrefix(prefix);
      payee.setName(name);
      payee.setAddress1(adr1);
      payee.setAddress2(adr2);
      payee.setCity(city);
      if (xRef != null) {
         if (xRef.indexOf('-') > 0) {
            payee.setPostalAccount(xRef);
         } else {
            payee.setIban(xRef);
         }
      }
      return payee;
   }

   private static final Logger logger = LoggerFactory.getLogger(DatabasePopulator.class);

   public static void main(String[] args) throws Exception {
      logger.info("Application started by {} at {} ", System.getProperty("user.name"), new Date());

      if (args.length == 0) {
         args = new String[] { "database-populator-context.xml" };
      }

      // Start the Spring container
      PersistenceHelper helper = (PersistenceHelper) new ClassPathXmlApplicationContext(args)
            .getBean(PersistenceHelper.class);
      System.out.println(helper.getAll(Currency.class));
   }
}
