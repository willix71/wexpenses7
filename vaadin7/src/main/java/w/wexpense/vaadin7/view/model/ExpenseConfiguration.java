package w.wexpense.vaadin7.view.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.utils.DateUtils;
import w.wexpense.model.Expense;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.filter.ExpenseFilter;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.ListView;

import com.vaadin.data.util.filter.Compare;

@Configuration
public class ExpenseConfiguration {

	@Bean
	@Scope("prototype")
	public ListView<Expense> expenseListView() {
		ListView<Expense> listview = getExpenseListView();       
		listview.addFilterSource(getExpenseFilter());		
		return listview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<Expense> todaysExpenseListView() {
		ListView<Expense> listview = getExpenseListView();
		listview.setFilter(new Compare.Greater("modifiedTs", DateUtils.toDate()));		
		listview.setViewName("Today's Expense");
		return listview;
	}
	
	@Bean
	@Scope("prototype")
	public ExpenseFilter getExpenseFilter() {
		return new ExpenseFilter();
	}
	
	private ListView<Expense> getExpenseListView() {
		ListView<Expense> listview = new ListView<Expense>(Expense.class);
		listview.setColumnConfigs(
	                new TableColumnConfig("id").rightAlign().collapse(),
	                new TableColumnConfig("uid").collapse(),
	                new TableColumnConfig("createdTs").collapse(),
	                new TableColumnConfig("modifiedTs").collapse(),
	                new TableColumnConfig("date").desc(),
	                new TableColumnConfig("payment.nextDate", "Payment"),
	                new TableColumnConfig("type").centerAlign(),
	                new TableColumnConfig("amount").rightAlign(),
	                new TableColumnConfig("currency").centerAlign(),
	                new TableColumnConfig("payee").sortBy(".display").expand(1.0f),              
	                new TableColumnConfig("externalReference").collapse(),
	                new TableColumnConfig("description").collapse(),
	                new TableColumnConfig("fileName").collapse(),
	                new TableColumnConfig("fileDate").collapse()
	                );
	       
	    ActionHelper.setDefaultListViewActions(listview, "expenseEditorView");
		return listview;
	}
}
