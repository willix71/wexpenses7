package w.wexpense.service.model.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;

import w.utils.DateBuilder;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseCriteria;
import w.wexpense.model.Payee;
import w.wexpense.model.QExpense;
import w.wexpense.model.QPayee;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;
import w.wexpense.persistence.dao.IExpenseJpaDao;
import w.wexpense.service.JpaRepoDaoService;
import w.wexpense.service.model.IExpenseService;
import w.wexpense.utils.ExpenseUtils;

@Service
public class ExpenseService extends JpaRepoDaoService<Expense, Long> implements IExpenseService {
	
	@Autowired 
	EntityManager manager;
	
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

	@Override
	public List<Expense> findExpenses(ExpenseCriteria criteria) {
		IExpenseJpaDao dao = (IExpenseJpaDao) getDao();
		
		//BooleanBuilder predicates = new BooleanBuilder();
		BooleanExpression predicate = null;
		if (criteria.getFromDate()!=null) {
			predicate = and(predicate, QExpense.expense.date.after(criteria.getFromDate()).or(QExpense.expense.date.eq(criteria.getFromDate())) );
		}
		if (criteria.getToDate()!=null) {
			predicate = and(predicate,QExpense.expense.date.before(criteria.getToDate()));
		}
		if (criteria.getFromAmount()!=null) {
			predicate = and(predicate,QExpense.expense.amount.goe(criteria.getFromAmount()));
		}
		if (criteria.getToAmount()!=null) {
			predicate = and(predicate,QExpense.expense.amount.lt(criteria.getToAmount()));
		}
		if (criteria.getCurrency()!=null) {
			predicate = and(predicate,QExpense.expense.currency.eq(criteria.getCurrency()));
		}
		if (criteria.getExpenseType()!=null) {
			predicate = and(predicate,QExpense.expense.type.eq(criteria.getExpenseType()));
		}
		if (criteria.getPayeeText()!=null && criteria.getPayeeText().trim().length()>0) {
			//predicate = and(predicate,QExpense.expense.payed.contains(criteria.getPayeeText()).or(QExpense.expense.payee.in(q)) );
			String text = criteria.getPayeeText().toLowerCase();
			QPayee pp = new QPayee("pp");
					
			JPQLQuery<Payee> q = new JPAQuery<Payee>().from(pp).where(pp.display.toLowerCase().contains(text));
			predicate = and(predicate, QExpense.expense.payee.in(q));
		}
		
		Sort order = new Sort(Direction.DESC, "date");
		if (predicate == null) {
			return dao.findAll(order);
		} else {
			return Lists.newArrayList(dao.findAll(predicate, order));
		}
	}
    
	private static BooleanExpression and(BooleanExpression p1, BooleanExpression p2) {
		return p1==null?p2:p1.and(p2);
	}
}
