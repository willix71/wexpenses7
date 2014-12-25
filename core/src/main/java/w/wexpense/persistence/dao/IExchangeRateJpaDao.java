package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.ExchangeRate;

public interface IExchangeRateJpaDao extends JpaRepository< ExchangeRate, Long >, JpaSpecificationExecutor< ExchangeRate >, IDBableJpaDao<ExchangeRate> {

}
