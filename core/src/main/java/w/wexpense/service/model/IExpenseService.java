package w.wexpense.service.model;

import java.util.Date;
import java.util.List;

import w.wexpense.model.Expense;
import w.wexpense.service.StorableService;

public interface IExpenseService extends StorableService<Expense, Long> {
    
    List<Expense> findSimiliarExpenses(Expense x);
    
    List<Expense> findExpenses(Date from, Date to);
}
