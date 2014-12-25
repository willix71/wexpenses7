package w.wexpense.vaadin7.action;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import w.wexpense.model.TransactionLine;
import w.wexpense.service.model.ITransactionLineService;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Table;

@Component
@Scope("prototype")
public class EditConsolidationTransactionLineAction extends EditAction {

	@Autowired
	private ITransactionLineService transactionLineService;
	
	public EditConsolidationTransactionLineAction() {
		super("expenseEditorView");
	}

	@Override
	public Serializable getInstanceId(Object sender, Object target) {
		com.vaadin.data.Container c = ((Table) sender) .getContainerDataSource();
		@SuppressWarnings("unchecked")
		BeanItem<TransactionLine> i = (BeanItem<TransactionLine>) c.getItem(target);
		return i.getBean().getExpense().getId();
	}
	
	@Override
   public Object getBean(Object sender, Object target, Object bean) {
      return transactionLineService.load(((TransactionLine) target).getId());
   }
}
