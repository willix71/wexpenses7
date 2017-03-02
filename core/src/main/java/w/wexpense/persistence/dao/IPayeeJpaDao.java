package w.wexpense.persistence.dao;

import w.wexpense.model.Payee;
import w.wexpense.persistence.IUidableDao;
import w.wexpense.persistence.IGenericDao;

public interface IPayeeJpaDao extends IGenericDao< Payee, Long >, IUidableDao<Payee>  {

}

