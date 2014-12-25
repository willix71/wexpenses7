package w.wexpense.vaadin7.view.model;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Duplicatable;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.model.ITransactionLineService;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.AddNewAction;
import w.wexpense.vaadin7.action.BalanceTransactionLineAction;
import w.wexpense.vaadin7.action.DuplicateAction;
import w.wexpense.vaadin7.action.EditAction;
import w.wexpense.vaadin7.action.RefreshAction;
import w.wexpense.vaadin7.filter.TransactionLineFilter;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.ListView;

import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.ui.Table;

@Configuration
public class TransactionLineConfiguration {

	@Autowired
	private ITransactionLineService transactionLineService;
	
	@Bean
	@Scope("prototype")
	public ListView<TransactionLine> transactionLineListView() {
		ListView<TransactionLine> listview = new ListView<TransactionLine>(TransactionLine.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("fullId").collapse().rightAlign(),
				   new TableColumnConfig("uid").collapse(),
				   new TableColumnConfig("createdTs").collapse(),
				   new TableColumnConfig("modifiedTs").collapse(),

				   new TableColumnConfig("date").rightAlign().asc(),
				   new TableColumnConfig("consolidation.date", "consolidation\nDate").collapse(),
				   new TableColumnConfig("expense.payee", "Payee").expand(1.0f),
				   new TableColumnConfig("account"),
				   new TableColumnConfig("discriminator"),
				   new TableColumnConfig("amount"),
				   new TableColumnConfig("expense.currency", "Currency"),
				   new TableColumnConfig("inValue", "In"),
				   new TableColumnConfig("outValue", "Out"),
				   new TableColumnConfig("balance").collapse(),
				   new TableColumnConfig("description").collapse()
				   );
		   
		listview.setActionHandler(getTransactionLineActionHandler("expenseEditorView"));
		listview.addFilterSource(getTransactionLineFilter());
		return listview;
	}	
	
	@Bean
	@Scope("prototype")
	public TransactionLineFilter getTransactionLineFilter() {
		return new TransactionLineFilter();
	}
	
	private ActionHandler getTransactionLineActionHandler(String editorName) {
		ActionHandler handler = new ActionHandler();
		handler.addListViewAction(new AddNewAction(editorName));
		handler.addListViewAction(new EditAction(editorName) {
			@Override
			public Serializable getInstanceId(Object sender, Object target) {
				com.vaadin.data.Container c = ((Table) sender) .getContainerDataSource();
				@SuppressWarnings("unchecked")
				JPAContainerItem<TransactionLine> i = (JPAContainerItem<TransactionLine>) c.getItem(target);
				return i.getEntity().getExpense().getId();
			}
		}, true);
		
		handler.addListViewAction(new DuplicateAction(editorName) {
		    @Override
            public Serializable getInstanceId(Object sender, Object target) {
                com.vaadin.data.Container c = ((Table) sender) .getContainerDataSource();
                @SuppressWarnings("unchecked")
                JPAContainerItem<TransactionLine> i = (JPAContainerItem<TransactionLine>) c.getItem(target);
                return i.getEntity().getExpense().getId();
            } 
		});
	      
		handler.addListViewAction(new RefreshAction());
		
		handler.addListViewAction(new BalanceTransactionLineAction(transactionLineService));
		
		return handler;
		
	}
}
