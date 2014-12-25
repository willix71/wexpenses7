package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.PayeeType;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

@Configuration
public class PayeeTypeConfiguration {

	@Autowired
	@Qualifier("payeeTypeService") 
	private StorableService<PayeeType, Long> payeeTypeService;
	
	
	@Bean
	@Scope("prototype")
	public EditorView<PayeeType, Long> payeeTypeEditorView() {
	    PropertyFieldLayout l = PropertyFieldHelper.getDBableFormPropertyFieldLayout("name","selectable");
		EditorView<PayeeType, Long> editorview = new EditorView<PayeeType, Long>(payeeTypeService, l);
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<PayeeType> payeeTypeListView() {
		ListView<PayeeType> listview = new ListView<PayeeType>(PayeeType.class);
		listview.setColumnConfigs(
				new TableColumnConfig("fullId").collapse().rightAlign(),
			   new TableColumnConfig("uid").collapse(),
			   new TableColumnConfig("createdTs").collapse(),
			   new TableColumnConfig("modifiedTs").collapse(),

			   new TableColumnConfig("name").asc(),
			   new TableColumnConfig("selectable").centerAlign()
			   );
		   
		ActionHelper.setDefaultListViewActions(listview, "payeeTypeEditorView");
		return listview;
	}
}
