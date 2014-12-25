package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Consolidation;

public interface IConsolidationJpaDao extends JpaRepository< Consolidation, Long >, JpaSpecificationExecutor< Consolidation >, IDBableJpaDao<Consolidation> {

}
