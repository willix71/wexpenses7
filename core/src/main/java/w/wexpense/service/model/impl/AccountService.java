package w.wexpense.service.model.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.Account;
import w.wexpense.persistence.dao.IAccountJpaDao;
import w.wexpense.service.DaoService;
import w.wexpense.service.instanciator.NameInitializor;
import w.wexpense.service.instanciator.ParentInitializor;
import w.wexpense.service.model.IAccountService;
import w.wexpense.utils.AccountUtils;

@Service
public class AccountService extends DaoService<Account, Long> implements IAccountService {

	@Autowired
	public AccountService(IAccountJpaDao dao) {
	   super(Account.class, dao, new ParentInitializor<Account>(Account.class), new NameInitializor<Account>(Account.class));
   }
	
	@Override
	public void renumberAccounts(Long topAccountId) {
		IAccountJpaDao dao = (IAccountJpaDao) getDao();
		
		if (topAccountId == null) {
			for(Account a : dao.findParents()) {
				AccountUtils.reNumber(a);
			}
		} else {
			AccountUtils.reNumber(dao.findOne(topAccountId)); 
		}
	}
}
