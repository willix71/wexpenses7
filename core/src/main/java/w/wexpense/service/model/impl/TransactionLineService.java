package w.wexpense.service.model.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.TransactionLine;
import w.wexpense.persistence.dao.ITransactionLineJpaDao;
import w.wexpense.service.JpaRepoDaoService;
import w.wexpense.service.model.ITransactionLineService;
import w.wexpense.utils.TransactionLineUtils;

@Service
public class TransactionLineService extends JpaRepoDaoService<TransactionLine, Long> implements ITransactionLineService {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	public TransactionLineService(ITransactionLineJpaDao dao) {
	   super(TransactionLine.class, dao);
   }

	@Override
	public void balanceTransactionLine() throws Exception {
		List<TransactionLine> tls = getDao().findAll();
		for(TransactionLine tl: tls) { tl.setBalance(null); };
		TransactionLineUtils.sortAndBalance(tls);
	}

}
