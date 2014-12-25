package w.wexpense.persistence.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.Consolidation;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;

public interface ITransactionLineJpaDao extends JpaRepository< TransactionLine, Long >, JpaSpecificationExecutor< TransactionLine >, IDBableJpaDao<TransactionLine> {
	List<TransactionLine> findByExpense(Expense expense);
	
	List<TransactionLine> findByConsolidation(Consolidation consolidation);
	
	@Query(value="from TransactionLine x where x.consolidation=?1 and x.uid not in (?2)")
	List<TransactionLine> findNotInConsolidation(Consolidation consolidation, Collection<String> uids);
}
