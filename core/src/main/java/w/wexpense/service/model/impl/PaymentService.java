package w.wexpense.service.model.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import w.wexpense.dta.DtaException;
import w.wexpense.dta.DtaHelper;
import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;
import w.wexpense.persistence.dao.IExpenseJpaDao;
import w.wexpense.persistence.dao.IPaymentDtaJpaDao;
import w.wexpense.persistence.dao.IPaymentJpaDao;
import w.wexpense.service.DaoService;
import w.wexpense.service.model.IPaymentService;
import w.wexpense.utils.DBableUtils;
import w.wexpense.utils.PaymentDtaUtils;

@Service
public class PaymentService extends DaoService<Payment, Long> implements IPaymentService {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private IExpenseJpaDao expenseDao;
	
	@Autowired
	private IPaymentDtaJpaDao paymentDtaDao;
	
	@Autowired
	public PaymentService(IPaymentJpaDao dao) {
	   super(Payment.class, dao);
   }
	
   @Override
   public Payment save(Payment entity) {
		if (entity.isNew()) {
			// hack needed because we can't add a existing expense to a new payment
			List<Expense> xs = entity.resetExpenses();
			
			Payment p = super.save(entity);

			if (!CollectionUtils.isEmpty(xs)) {
				for(Expense x:xs) {
					p.addExpense(expenseDao.save(x));
				}
			}
			return p;
		} else {
         // update all expenses which were removed from the payment's expenses
         List<Expense> xs = CollectionUtils.isEmpty(entity.getExpenses())?
               expenseDao.findByPayment(entity):
               expenseDao.findNotInPayment(entity, DBableUtils.getUids(entity.getExpenses()));
         if (!CollectionUtils.isEmpty(xs)) {
            for(Expense x: xs) {          
               x.setPayment(null);
            }
         }
         
		   for(Expense x: entity.getExpenses()) {
		      if (x.isNew()) {
		         expenseDao.save(x);
		      }
		   }
			
			Payment p = super.save(entity);		
			return p;	
		}
   }
    
	@Override
	public Payment generatePaymentDtas(Payment payment) throws DtaException {
		boolean isNew = payment.isNew();
		
		if (payment.getDate() == null) {
			payment.setDate(new Date());
		}
		
		String filename = payment.getFilename();
		if (filename == null || Payment.DEFAULT_FILENAME.equals(filename)) {
			filename = MessageFormat.format("{0,date,yyyy-MM-dd}_{1}.DTA", payment.getDate(), DtaHelper.APPLICATION_ID);
			payment.setFilename(filename);
		}
		
		payment.setSelectable(false);
		
		payment = save(payment);

		if (!isNew) {
			// delete existing dtas		
			Query query = entityManager.createQuery("DELETE PaymentDta WHERE payment = :p");
			query.setParameter("p", payment);
			int i = query.executeUpdate();
			LOGGER.info("Deleted {} old payment DTAs", i);
		}
		
		LOGGER.info("Creating new payment DTAs for {} expenses", payment.getExpenses().size());
		
		if (payment.getDtaLines() != null) {
			payment.getDtaLines().clear();
		} else {
			payment.setDtaLines(new ArrayList<PaymentDta>());
		}
		
		// generate dtas and save them
		for(PaymentDta dta : PaymentDtaUtils.getPaymentDtas(payment)) {
			payment.getDtaLines().add(paymentDtaDao.save(dta));
		}
		
		LOGGER.info("Created {} new payment DTAs", payment.getDtaLines().size());
		
		return payment;
	}
	
	public Payment getPaymentByUid(String uid) throws Exception {
		return ((IPaymentJpaDao) getDao()).findByUid(uid);
	}
	
	public Payment getPaymentByFilename(String filename) throws Exception {
		return ((IPaymentJpaDao) getDao()).findByUid(filename);
	}
}
