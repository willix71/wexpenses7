package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.ExpenseType;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

@Configuration
public class ExpenseTypeConfiguration {

	@Autowired
	@Qualifier("expenseTypeService") 
	private StorableService<ExpenseType, Long> expenseTypeService;
	
	@Bean
	@Scope("prototype")
	public EditorView<ExpenseType, Long> expenseTypeEditorView() {
	    PropertyFieldLayout l = PropertyFieldHelper.getDBableFormPropertyFieldLayout("name","selectable","paymentGeneratorClassName");
		EditorView<ExpenseType, Long> editorview = new EditorView<ExpenseType, Long>(expenseTypeService, l);
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<ExpenseType> expenseTypeListView() {
		ListView<ExpenseType> listview = new ListView<ExpenseType>(ExpenseType.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("id").collapse().rightAlign(),
				   new TableColumnConfig("uid").collapse(),
				   new TableColumnConfig("createdTs").collapse(),
				   new TableColumnConfig("modifiedTs").collapse(),

				   new TableColumnConfig("name").asc(),
				   new TableColumnConfig("selectable").centerAlign(),
				   new TableColumnConfig("paymentGeneratorClassName", "Generator")
				   );
		   
		ActionHelper.setDefaultListViewActions(listview, "expenseTypeEditorView");
		return listview;
	}
}
