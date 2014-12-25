package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.Account;
import w.wexpense.model.Payee;

public interface IAccountJpaDao extends JpaRepository< Account, Long >, JpaSpecificationExecutor< Account >, IDBableJpaDao<Account>  {

	@Query("from Account a where a.parent is null")
	List<Account> findParents();

	List<Account> findByOwner(Payee owner);
	
	@Query("from Account a where a.owner = ?1 or a.owner.bankDetails = ?1")
	List<Account> findByOwnerAndBankDetails(Payee owner);
}
