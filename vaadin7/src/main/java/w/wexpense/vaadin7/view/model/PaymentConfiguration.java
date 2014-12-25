package w.wexpense.vaadin7.view.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.MultiSelectorView;

@Configuration
public class PaymentConfiguration {

	@Bean
	@Scope("prototype")
	public ListView<Payment> paymentListView() {
		ListView<Payment> listview = new ListView<Payment>(Payment.class);
		listview.setColumnConfigs(
				new TableColumnConfig("id").collapse().rightAlign(), 
				new TableColumnConfig("uid").collapse(),
		      new TableColumnConfig("createdTs").collapse(), 
		      new TableColumnConfig("modifiedTs").collapse(),

		      new TableColumnConfig("date").desc(), 
		      new TableColumnConfig("filename"), 
		      new TableColumnConfig("selectable"));

		ActionHelper.setDefaultListViewActions(listview, "paymentEditorView");
		return listview;
	}
	
	@Bean
	@Scope("prototype")
	public MultiSelectorView<Expense> paymentExpenseSelectorView() {
		MultiSelectorView<Expense> selector = new MultiSelectorView<Expense>(Expense.class);
		selector.setColumnConfigs(getExpenseTableColumnConfig());
		return selector;
	}
	
	
	public static TableColumnConfig[] getExpenseTableColumnConfig() {
		return new TableColumnConfig[] {
				new TableColumnConfig("fullId").collapse().rightAlign(),
            new TableColumnConfig("uid").collapse(),
            new TableColumnConfig("createdTs").collapse(),
            new TableColumnConfig("modifiedTs").collapse(),
           
            new TableColumnConfig("date").desc(),
            new TableColumnConfig("type").centerAlign(),
            new TableColumnConfig("amount").rightAlign(),
            new TableColumnConfig("currency").centerAlign(),
            new TableColumnConfig("payee").expand(1.0f),           
            new TableColumnConfig("externalReference").collapse(),
            new TableColumnConfig("description").collapse()
		};
	}
}
