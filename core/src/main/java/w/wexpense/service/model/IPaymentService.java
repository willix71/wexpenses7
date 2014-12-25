package w.wexpense.service.model;

import org.springframework.transaction.annotation.Transactional;

import w.wexpense.dta.DtaException;
import w.wexpense.model.Payment;
import w.wexpense.service.StorableService;

public interface IPaymentService extends StorableService<Payment, Long> {
    
	@Transactional
	Payment generatePaymentDtas(Payment payment) throws DtaException;
}
