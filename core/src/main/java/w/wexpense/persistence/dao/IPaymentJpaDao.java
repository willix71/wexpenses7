package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Payment;

public interface IPaymentJpaDao extends JpaRepository< Payment, Long >, JpaSpecificationExecutor< Payment > {
	Payment findByUid(String uid);
	
	Payment findByFilename(String filename);
}
