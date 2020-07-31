package w.wexpense.persistence.dao;

import w.wexpense.model.PayeeType;
import w.wexpense.persistence.IGenericDao;
import w.wexpense.persistence.ITypeDao;
import w.wexpense.persistence.IUidableDao;

public interface IPayeeTypeJpaDao extends IGenericDao< PayeeType, Long >, IUidableDao<PayeeType>, ITypeDao<PayeeType>  { }
