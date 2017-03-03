package w.wexpense.jsf.controller;

import java.util.Date;
import java.util.List;

import javax.faces.bean.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import w.utils.DateBuilder;
import w.wexpense.model.Expense;
import w.wexpense.service.model.IExpenseService;

@Component("jsfExpenseController")
@RequestScoped
public class ExpenseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseController.class);
	
	@Autowired
	private IExpenseService expenseService;

	private Date from;
	private Date to;
	private List<Expense> expenses;

	public ExpenseController() {
		DateBuilder sb = new DateBuilder();
		from = sb.startOfMonth().startOfDay().toDate();
		to = sb.endOfMonth().endOfDay().toDate();
	}
	
	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = DateBuilder.from(from).startOfDay().toDate();
		LOGGER.warn("Set from filter {}", this.from);
	}
	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = DateBuilder.from(to).endOfDay().toDate();
		LOGGER.warn("Set to filter {}", this.to);
	}

	public List<Expense> getExpenses() {
		return expenses;
	}

	public String filter() {
		LOGGER.warn("Filtering between {} and {}", from, to);
		expenses = expenseService.findExpenses(from, to);
		return null;
	}
}
