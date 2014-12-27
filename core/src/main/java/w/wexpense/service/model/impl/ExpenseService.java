package w.wexpense.service.model.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.wexpense.model.Expense;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.persistence.dao.IExpenseJpaDao;
import w.wexpense.service.DaoService;
import w.wexpense.service.model.IExpenseService;
import w.wexpense.utils.ExpenseUtils;

@Service
public class ExpenseService extends DaoService<Expense, Long> implements IExpenseService {
	
	@Autowired
	public ExpenseService(IExpenseJpaDao dao) {
		super(Expense.class, dao);
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
		ExpenseUtils.addTransactionLine(x, null, TransactionLineEnum.OUT);
		ExpenseUtils.addTransactionLine(x, null, TransactionLineEnum.IN);
		return x;
	}
	

    @Override
    public List<Expense> findSimiliarExpenses(Date d, BigDecimal amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.clear();
        Date d1 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date d2 = cal.getTime();
        return ((IExpenseJpaDao) getDao()).findSimiliarExpenses(d1, d2, amount);
    }
	
//	@Autowired
//	private ITransactionLineJpaDao transactionLineDao;
		
//	@Override
//   public Expense save(Expense entity) {
//	   LOGGER.debug("Saving expense {}", entity);
//	   
//	   List<TransactionLine> newTransactionLines = entity.getTransactions();
//	   
//	   Expense newExpense = super.save(entity);
//	   
//	   List<TransactionLine> oldTransactionLines = transactionLineDao.findByExpense(newExpense);	   
//	   LOGGER.debug("old expense's transaction line size{}", oldTransactionLines.size());
//	   
//	   for(TransactionLine newTransactionLine: newTransactionLines) {
//	   	newTransactionLine.setExpense(newExpense);
//	   	transactionLineDao.save(newTransactionLine);
//	   }
//	   for(TransactionLine oldTransactionLine: oldTransactionLines) {
//	   	if (!newTransactionLines.contains(oldTransactionLine)) {
//	   		transactionLineDao.delete(oldTransactionLine);	   		
//	   	}
//	   }
//	   return newExpense;
//	}
}
