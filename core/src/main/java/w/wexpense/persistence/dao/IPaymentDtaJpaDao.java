package w.wexpense.persistence.dao;

import w.wexpense.model.PaymentDta;
import w.wexpense.persistence.IUidableDao;
import w.wexpense.persistence.IGenericDao;

public interface IPaymentDtaJpaDao extends IGenericDao< PaymentDta, Long >, IUidableDao<PaymentDta> {

}
