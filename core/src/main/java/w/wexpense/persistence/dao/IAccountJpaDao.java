package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.Account;
import w.wexpense.model.Payee;
import w.wexpense.persistence.IUidableDao;
import w.wexpense.persistence.IGenericDao;

public interface IAccountJpaDao extends IGenericDao< Account, Long >, IUidableDao<Account>  {

	@Query("from Account a where a.parent is null")
	List<Account> findParents();

	List<Account> findByOwner(Payee owner);
	
	@Query("from Account a where a.owner = ?1 or a.owner.bankDetails = ?1")
	List<Account> findByOwnerAndBankDetails(Payee owner);
	
	@Query("from Account a where a.fullName like ?1 or a.fullNumber like ?1")
	List<Account> findByFullNameOrByFullNumber(String criteria);
}
