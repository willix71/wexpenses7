package w.wexpense.persistence.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;
import w.wexpense.persistence.IUidableDao;
import w.wexpense.persistence.IGenericDao;

public interface IExpenseJpaDao extends IGenericDao<Expense, Long>, IUidableDao<Expense> {
	List<Expense> findByPayee(Payee p);

	List<Expense> findByPayment(Payment p);

	@Query(value = "from Expense x where x.payment=?1 and x.uid not in (?2)")
	List<Expense> findNotInPayment(Payment p, Collection<String> uids);

	@Query(value = "from Expense x where x.date>=?1 and x.date<=?2 order by x.date DESC")
	List<Expense> findExpenses(Date from, Date to);

	@Query(value = "from Expense x where x.date>=?1 and x.date<=?2 and x.amount = ?3")
	List<Expense> findSimiliarExpenses(Date d1, Date d2, BigDecimal amount);

	@Query(value = "from Expense x where x.date>=?1 and x.date<=?2 and x.amount = ?3 and x != ?4")
	List<Expense> findSimiliarExpenses(Date d1, Date d2, BigDecimal amount, Expense x);

}
