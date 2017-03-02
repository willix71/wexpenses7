package w.wexpense.persistence.dao;

import w.wexpense.model.ExchangeRate;
import w.wexpense.persistence.IUidableDao;
import w.wexpense.persistence.IGenericDao;

public interface IExchangeRateJpaDao extends IGenericDao< ExchangeRate, Long >, IUidableDao<ExchangeRate> {

}
