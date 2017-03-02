package w.wexpense.service.model.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.Account;
import w.wexpense.persistence.dao.IAccountJpaDao;
import w.wexpense.service.JpaRepoDaoService;
import w.wexpense.service.instanciator.NameInitializor;
import w.wexpense.service.instanciator.ParentInitializor;
import w.wexpense.service.model.IAccountService;
import w.wexpense.utils.AccountUtils;

@Service
public class AccountService extends JpaRepoDaoService<Account, Long> implements IAccountService {

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

	@Override
	public List<Account> easyFind(String criteria) {
		IAccountJpaDao dao = (IAccountJpaDao) getDao();
		if (criteria == null) 
			return dao.findAll();
		
		if (criteria.contains("*")) 
			criteria = criteria.replaceFirst("*", "%");
		else 
			criteria = "%" + criteria + "%";
		
        return dao.findByFullNameOrByFullNumber(criteria);
	}
}
