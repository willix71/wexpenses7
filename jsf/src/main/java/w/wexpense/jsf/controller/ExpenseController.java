package w.wexpense.jsf.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.utils.DateBuilder;
import w.wexpense.jsf.config.ServiceLocator;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseCriteria;
import w.wexpense.service.model.IExpenseService;

@ManagedBean(name = "jsfExpenseController")
@ViewScoped
public class ExpenseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseController.class);
	
	private ExpenseCriteria criteria;
	private List<Expense> expenses;
	private Expense selectedExpense;
	
	public ExpenseController() {
		LOGGER.warn("New ExpenseController");
		init();
	}
	
	private void init() {
		criteria = new ExpenseCriteria();
		DateBuilder sb = new DateBuilder();
		criteria.setFromDate( sb.startOfMonth().startOfDay().toDate() );
		criteria.setToDate( sb.endOfMonth().endOfDay().toDate() );
	}
	
	public void setFilterNow(boolean anything) {
		if (anything) filter();
	}
	
	public boolean getFilterNow() {
		return true;
	}
	
	public ExpenseCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ExpenseCriteria criteria) {
		this.criteria = criteria;
	}

	public List<Expense> getExpenses() {
		return expenses;
	}
	
	public void filter() {
		LOGGER.warn("Filtering expenses with {}", criteria);
		expenses = new ArrayList<>(ServiceLocator.getService(IExpenseService.class).findExpenses(criteria));
	}

	public void reset() {
		LOGGER.warn("Reseting criteria");
		init();
		filter();
	}
	
	public Expense getSelectedExpense() {
		return selectedExpense;
	}

	public void setSelectedExpense(Expense selectedExpense) {
		this.selectedExpense = selectedExpense;
	}
	
	public void deleteExpense() {
		LOGGER.warn("Deleting expense {}", selectedExpense);
	}
}
