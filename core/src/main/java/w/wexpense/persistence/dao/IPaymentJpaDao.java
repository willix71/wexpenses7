package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import w.wexpense.model.Payment;

public interface IPaymentJpaDao extends JpaRepository< Payment, Long >, JpaSpecificationExecutor< Payment > {
	
   Payment findByUid(String uid);
	
	Payment findByFilename(String filename);
	
   @Query(value="from Payment x where x.selectable = true order by date asc, createdTs asc")
   List<Payment> findNextPayment();
}
