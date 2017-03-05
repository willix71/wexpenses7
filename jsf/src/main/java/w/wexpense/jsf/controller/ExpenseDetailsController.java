package w.wexpense.jsf.controller;

import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import w.wexpense.model.Expense;
import w.wexpense.service.model.IExpenseService;

@Component("jsfExpenseDetailsController")
@SessionScoped
public class ExpenseDetailsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseDetailsController.class);
	
	@Autowired
	private IExpenseService expenseService;

	private String expenseUid;
	private Expense expense = new Expense();

	private ExpenseDetailsController() {
		LOGGER.debug("Created");
	}
	
	public Expense getExpense() {
		return expense;
	}
	
	public void setExpense(Expense e) {
		LOGGER.warn("Setter is being called");
	}
	public String getExpenseUid() {
		return expenseUid;
	}

	public void setExpenseUid(String expenseUid) {
		LOGGER.debug("loading expense {}", expenseUid);
		this.expenseUid = expenseUid;
		this.expense = expenseService.loadByUid(expenseUid);
	}

	public String saveExpense() {
		LOGGER.debug("Saving expense {}", expenseUid);
		expense = expenseService.save(expense);
		return null;
	}
	
	public String refreshExpense() {
		LOGGER.debug("refreshing expense {}", expenseUid);
		this.expense = expenseService.loadByUid(expenseUid);
		return null;
	}
}
