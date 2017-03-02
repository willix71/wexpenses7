package w.wexpense.persistence.dao;

import w.wexpense.model.City;
import w.wexpense.persistence.IUidableDao;
import w.wexpense.persistence.IGenericDao;

public interface ICityJpaDao extends IGenericDao< City, Long >, IUidableDao<City>  {

}

