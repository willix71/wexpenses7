package w.wexpense.persistence.dao;

import w.wexpense.model.Consolidation;
import w.wexpense.persistence.IUidableDao;
import w.wexpense.persistence.IGenericDao;

public interface IConsolidationJpaDao extends IGenericDao< Consolidation, Long >, IUidableDao<Consolidation> {

}
