package w.wexpense.service.model.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.Account;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.persistence.dao.IExchangeRateJpaDao;
import w.wexpense.service.JpaRepoDaoService;
import w.wexpense.service.instanciator.Initializor;
import w.wexpense.service.model.IExchangeRateService;

@Service
public class ExchangeRateService extends JpaRepoDaoService<ExchangeRate, Long> implements IExchangeRateService {

	@Autowired
	public ExchangeRateService(IExchangeRateJpaDao dao) {
		super(ExchangeRate.class, dao, new Initializor<ExchangeRate>() {
			@Override
			public Object[] initialize(ExchangeRate xRate, Object[] args) {
				if (args == null || args.length == 0 || !(args[0] instanceof TransactionLine)) return args;

				TransactionLine tl = (TransactionLine) args[0];

				Expense x = tl.getExpense();
				if (x != null) {
					xRate.setDate(x.getDate());
					xRate.setInstitution(x.getPayee());
					xRate.setFromCurrency(x.getCurrency());
				}

				Account acc = tl.getAccount();
				if (acc != null) {
					xRate.setToCurrency(acc.getCurrency());
					
					if (acc.getOwner() != null) {
						if (acc.getOwner().getBankDetails() != null) {
							xRate.setInstitution(acc.getOwner().getBankDetails());
						} else {
							xRate.setInstitution(acc.getOwner());
						}
					}
				}

				return Arrays.copyOfRange(args, 1, args.length);
			}
		});
	}
}
