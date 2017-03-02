package w.wexpense.persistence.dao;

import w.wexpense.model.Repeater;
import w.wexpense.persistence.IUidableDao;
import w.wexpense.persistence.IGenericDao;

public interface IRepeaterJpaDao extends IGenericDao< Repeater, Long >, IUidableDao<Repeater>  {

}

