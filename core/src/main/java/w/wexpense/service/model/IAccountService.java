package w.wexpense.service.model;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import w.wexpense.model.Account;
import w.wexpense.service.StorableService;

public interface IAccountService extends StorableService<Account, Long> {

	@Transactional
	void renumberAccounts(Long topAccountId);

	List<Account> easyFind(String criteria);
}
