package w.wexpense.service.model.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import w.wexpense.model.Account;
import w.wexpense.model.Consolidation;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.persistence.dao.IAccountJpaDao;
import w.wexpense.persistence.dao.IConsolidationJpaDao;
import w.wexpense.persistence.dao.ITransactionLineJpaDao;
import w.wexpense.service.JpaRepoDaoService;
import w.wexpense.service.instanciator.Initializor;
import w.wexpense.service.model.IConsolidationService;
import w.wexpense.utils.DBableUtils;

@Service
public class ConsolidationService extends JpaRepoDaoService<Consolidation, Long> implements IConsolidationService {

	@Autowired
	private IAccountJpaDao accountDao;
	
	@Autowired
	private ITransactionLineJpaDao transationLineDao;
	
	@Autowired
	public ConsolidationService(IConsolidationJpaDao dao) {
	   super(Consolidation.class, dao, new Initializor<Consolidation>() {
		   @Override
		   public Object[] initialize(Consolidation c, Object[] args) {
			  	if (args==null || args.length == 0 ||  !(args[0] instanceof Consolidation)) return args;
			  
			  	Consolidation lastConsolidation = (Consolidation) args[0];
			  	c.setInstitution(lastConsolidation.getInstitution());			  	
			  	c.setOpeningBalance(lastConsolidation.getClosingBalance());
			  	
			  	Calendar cal=Calendar.getInstance();
			  	cal.setTime(lastConsolidation.getDate());
			  	cal.add(Calendar.MONTH, 1);
			  	c.setDate(cal.getTime());
			  	
			  	return Arrays.copyOfRange(args, 1, args.length);
		   }
	   });
   }
	
	@Override
   public Consolidation save(Consolidation entity) {
		if (entity.isNew()) {
			// hack needed because we can't add a existing expense to a new payment
			List<TransactionLine> xs = entity.resetTransaction();
			
			Consolidation conso = super.save(entity);

			if (!CollectionUtils.isEmpty(xs)) {
				for(TransactionLine x:xs) {
					conso.addTransaction(transationLineDao.save(x));
				}
			}
			return conso;
		} else {		
         // update all expenses which were removed from the payment's expenses
         List<TransactionLine> xs = CollectionUtils.isEmpty(entity.getTransactions())?
               transationLineDao.findByConsolidation(entity):
               transationLineDao.findNotInConsolidation(entity, DBableUtils.getUids(entity.getTransactions()));
         if (!CollectionUtils.isEmpty(xs)) {
            for(TransactionLine x: xs) {           
               x.setConsolidation(null);
            }
         }
         
         for (TransactionLine line : entity.getTransactions()) {
            if (line.isNew()) {
               transationLineDao.save(line);
            }
         }

			Consolidation conso = super.save(entity);	
			return conso;	
		}
   }

	@Override
   public List<Account> getConsolidationAccounts(Payee institution) {
	   return accountDao.findByOwnerAndBankDetails(institution);
   }
  
}
