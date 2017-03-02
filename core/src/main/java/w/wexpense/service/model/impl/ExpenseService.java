package w.wexpense.service.model.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import w.utils.DateBuilder;
import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.persistence.dao.IExpenseJpaDao;
import w.wexpense.service.JpaRepoDaoService;
import w.wexpense.service.model.IExpenseService;
import w.wexpense.utils.ExpenseUtils;

@Service
public class ExpenseService extends JpaRepoDaoService<Expense, Long> implements IExpenseService {
	
	@Autowired
	public ExpenseService(IExpenseJpaDao dao) {
		super(Expense.class, dao);
	}

	@Override
	@Transactional
	public Expense load(Long id) {
		Expense x = super.load(id);
		
		// make sure we load the transaction lines as well		
		if (x!=null) for(TransactionLine l : x.getTransactions()) l.getExchangeRate();

		return x;
	}

	@Override
	public Expense save(Expense entity) {
		LOGGER.debug("Saving expense {}", entity);

		Expense newExpense = super.save(entity);

		return newExpense;
	}

	@Override
	public Expense newInstance(Object... args) {
		Expense x = new Expense();
		ExpenseUtils.newTransactionLine(x, TransactionLineEnum.OUT);
		ExpenseUtils.newTransactionLine(x, TransactionLineEnum.IN);
		return x;
	}

	@Override
    public List<Expense> findSimiliarExpenses(Expense x) {
		DateBuilder db = new DateBuilder(x.getDate());
		Date d1 = db.startOfDay().toDate();
		Date d2 = db.endOfDay().toDate();
        
        IExpenseJpaDao dao = (IExpenseJpaDao) getDao();
        return x.isNew()?dao.findSimiliarExpenses(d1, d2, x.getAmount()):dao.findSimiliarExpenses(d1, d2, x.getAmount(), x);
    }
	
    @Override
	public List<Expense> findExpenses(Date from, Date to) {
    	IExpenseJpaDao dao = (IExpenseJpaDao) getDao();
        return dao.findExpenses(from, to);
	}
}
