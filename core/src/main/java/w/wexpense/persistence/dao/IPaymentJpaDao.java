package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.Payment;
import w.wexpense.persistence.IUidableDao;
import w.wexpense.persistence.IGenericDao;

public interface IPaymentJpaDao extends IGenericDao<Payment, Long>, IUidableDao<Payment> {

	Payment findByFilename(String filename);

	@Query(value = "from Payment x where x.selectable = true order by date asc, createdTs asc")
	List<Payment> findNextPayment();
}
