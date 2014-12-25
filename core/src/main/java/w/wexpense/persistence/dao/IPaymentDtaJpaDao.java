package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.PaymentDta;

public interface IPaymentDtaJpaDao extends JpaRepository< PaymentDta, Long >, JpaSpecificationExecutor< PaymentDta >, IDBableJpaDao<PaymentDta> {

}
