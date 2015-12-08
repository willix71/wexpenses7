package w.wexpense.persistence;

import org.springframework.context.annotation.Configuration;

import w.utils.DateUtils;
import w.wexpense.dta.BvoDtaFormater;
import w.wexpense.dta.BvrDtaFormater;
import w.wexpense.dta.IbanDtaFormater;
import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Country;
import w.wexpense.model.Currency;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.PayeeType;
import w.wexpense.model.enums.AccountEnum;
import w.wexpense.test.config.DatabasePopulationConfig;
import w.wexpense.utils.AccountUtils;
import w.wexpense.utils.ExchangeRateUtils;
import w.wexpense.utils.ExpenseUtils;
import w.wexpense.utils.PayeeUtils;

@Configuration
public class DefaultDatabasePopulationConfig extends DatabasePopulationConfig {
	public DefaultDatabasePopulationConfig() {
		Currency chf = add(new Currency("CHF", "Swiss Francs", 20));
		Currency euro = add(new Currency("EUR", "Euro", 100));
		Currency usd = add(new Currency("USD", "US Dollar", 100));
		Currency gbp = add(new Currency("GBP", "British Pounds", 100));

		Country ch = add(new Country("CH", "Switzerland", chf));
		Country uk = add(new Country("UK", "United Kingdom", gbp));
		Country f = add(new Country("FR", "France", euro));
		add(new Country("IT", "Italie", euro));
		add(new Country("DE", "Germany", euro));
		add(new Country("US", "United States of America", usd));

		add(new City(null, "London", uk));
		add(new City(null, "Paris", f));
		City nyon = add(new City("1260", "Nyon", ch));
		City gland = add(new City("1196", "Gland", ch));
		City prangins = add(new City("1197", "Prangins", ch));
		City lausanne = add(new City("1001", "Lausanne", ch));
		City zoug = add(new City("6000", "Zoug ", ch));
		City zurich = add(new City("8000", "Zurich", ch));
		
	    add(new ExpenseType("BVO", true, BvoDtaFormater.class.getName()));
	    add(new ExpenseType("BVR", true, BvrDtaFormater.class.getName()));
	    add(new ExpenseType("iban", true, IbanDtaFormater.class.getName()));
	    ExpenseType recu = add(new ExpenseType("recu", true, null));
	    
		add(ExchangeRateUtils.newExchangeRate(euro,chf,DateUtils.toDate(1,1,2000),1.6));
		add(ExchangeRateUtils.newExchangeRate(euro,chf,DateUtils.toDate(1,1,2015),1.2));
		add(ExchangeRateUtils.newExchangeRate(gbp,chf,DateUtils.toDate(1,1,2015),1.48315));
		
		PayeeType phone = add(new PayeeType("Phone"));
		PayeeType wine = add(new PayeeType("Wine"));
		
		Payee wk = add(PayeeUtils.newPayee("William Keyser","11 ch. du Grand Noyer", prangins, null, "CH650022822851333340B"));
		Payee bcv = add(PayeeUtils.newPayee("Banque Cantonale Vaudoise",lausanne));
		Payee blondel = add(PayeeUtils.newPayee(wine, "Blondel Yves", "Rue du mont-le-grand 32", gland, null,"CH8400767000T09306497", bcv));
		Payee talktalk = add(PayeeUtils.newPayee(phone, "Talk Talk AG", "Case Postal 1359", zoug, "01-2546-3"));
		Payee sunrise = add(PayeeUtils.newPayee(phone, "Sunrise", "Binzmuhlestrasse 130", zurich, "01-053099-3"));
		Payee migros = add(PayeeUtils.newPayee("Migros", nyon));
		
		Account asset = add(AccountUtils.newAccount(1,"Asset", AccountEnum.ASSET, chf));
		Account cash = add(AccountUtils.newAccount(asset, 2,"Cash", AccountEnum.ASSET, chf));
		Account xs = add(AccountUtils.newAccount(3,"Expense", AccountEnum.EXPENSE, chf));
		Account food = add(AccountUtils.newAccount(xs, 4,"Food", AccountEnum.EXPENSE, chf));
		Account car = add(AccountUtils.newAccount(xs, 5,"Car", AccountEnum.EXPENSE, chf));
		Account misc = add(AccountUtils.newAccount(xs, 6,"Misc", AccountEnum.EXPENSE, chf));
		
		Expense x1 = add(ExpenseUtils.newExpense(recu, DateUtils.toDate(1,2,2015), 100, migros, cash, car ));
		addAll(x1.getTransactions());
//
//		Expense x2 =  add(ExpenseUtils.newExpense(recu, DateUtils.toDate(2,2,2015), 86, migros, cash, misc ));
//		addAll(x2.getTransactions());
	};
}