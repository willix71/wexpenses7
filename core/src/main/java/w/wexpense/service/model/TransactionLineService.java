package w.wexpense.service.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.TransactionLine;
import w.wexpense.persistence.dao.ITransactionLineJpaDao;
import w.wexpense.service.DaoService;
import w.wexpense.utils.TransactionLineUtils;

@Service
public class TransactionLineService extends DaoService<TransactionLine, Long> implements ITransactionLineService {
	
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
