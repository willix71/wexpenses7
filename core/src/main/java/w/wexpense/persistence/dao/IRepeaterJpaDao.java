package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Repeater;

public interface IRepeaterJpaDao extends JpaRepository< Repeater, Long >, JpaSpecificationExecutor< Repeater >, IDBableJpaDao<Repeater>  {

}

