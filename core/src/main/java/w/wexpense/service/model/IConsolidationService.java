package w.wexpense.service.model;

import java.util.List;

import w.wexpense.model.Account;
import w.wexpense.model.Consolidation;
import w.wexpense.model.Payee;
import w.wexpense.service.StorableService;

public interface IConsolidationService extends StorableService<Consolidation, Long> {

	List<Account> getConsolidationAccounts(Payee institution);
	
}
