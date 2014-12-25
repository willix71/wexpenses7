package w.wexpense.service.model;

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
import w.wexpense.service.DaoService;
import w.wexpense.utils.DBableUtils;

@Service
public class ConsolidationService extends DaoService<Consolidation, Long> implements IConsolidationService {

	@Autowired
	private IAccountJpaDao accountDao;
	
	@Autowired
	private ITransactionLineJpaDao transationLineDao;
	
	@Autowired
	public ConsolidationService(IConsolidationJpaDao dao) {
	   super(Consolidation.class, dao);
   }
	
	@Override
   public Consolidation save(Consolidation entity) {
		if (entity.isNew()) {
			// hack needed because we can't add a existing expense to a new payment
			List<TransactionLine> xs = entity.resetTransaction();
			
			Consolidation conso = super.save(entity);

			if (!CollectionUtils.isEmpty(xs)) {
				for(TransactionLine x:xs) {
					x.setConsolidation(entity);
					conso.getTransactions().add(transationLineDao.save(x));
				}
			}
			return conso;
		} else {		
			// update all expenses which were removed from the payment's expenses
			List<TransactionLine> xs = CollectionUtils.isEmpty(entity.getTransactions())?transationLineDao.findByConsolidation(entity):transationLineDao.findNotInConsolidation(entity, DBableUtils.getUids(entity.getTransactions()));
			if (!CollectionUtils.isEmpty(xs)) {
				for(TransactionLine x: xs) {				
					x.setConsolidation(null);
				}
			}
			
			// make sure the all transactions are set to this consolidation
			if (!CollectionUtils.isEmpty(entity.getTransactions())) {
				for(TransactionLine x: entity.getTransactions()) {				
					if (x.getConsolidation() == null || !x.getConsolidation().equals(entity)) {
						x.setConsolidation(entity);
					}
				}
			}

			Consolidation conso = super.save(entity);	
			return conso;	
		}
   }
		
//   public Consolidation saveOld(Consolidation entity) {
//	   LOGGER.debug("Saving consolidation's transactions {}", entity.getTransactions());
//	   
//	   List<TransactionLine> newTransactionLines = entity.getTransactions();
//	   
//	   Consolidation newConsolidation = super.save(entity);
//	   
//	   List<TransactionLine> oldTransactionLines = transationLineDao.findByConsolidation(newConsolidation);	   
//	   LOGGER.debug("old payment's expenses size{}", oldTransactionLines.size());
//	   
//	   for(TransactionLine newTransactionLine: newTransactionLines) {
//	   	newTransactionLine.setConsolidation(newConsolidation);
//	   	transationLineDao.save(newTransactionLine);
//	   }
//	   for(TransactionLine oldTransactionLine: oldTransactionLines) {
//	   	if (!newTransactionLines.contains(oldTransactionLine)) {
//	   		oldTransactionLine.setConsolidation(null);
//	   		transationLineDao.save(oldTransactionLine);
//	   	}
//	   }
//	   return newConsolidation;
//   }

	@Override
   public List<Account> getConsolidationAccounts(Payee institution) {
	   return accountDao.findByOwnerAndBankDetails(institution);
   }
  
}
