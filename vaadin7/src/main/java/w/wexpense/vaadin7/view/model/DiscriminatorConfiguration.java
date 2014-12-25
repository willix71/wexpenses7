package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Discriminator;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.component.RelationalFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

@Configuration
public class DiscriminatorConfiguration {

	@Autowired
	@Qualifier("discriminatorService")
	private StorableService<Discriminator, Long> discriminatorService;
	
	@Bean
	@Scope("prototype")
	public EditorView<Discriminator, Long> discriminatorEditorView() {
	    PropertyFieldLayout l = PropertyFieldHelper.getDBableFormPropertyFieldLayout("parent","name","selectable");
		EditorView<Discriminator, Long> editorview = new EditorView<Discriminator, Long>(discriminatorService, l);
		editorview.setRelationalFieldCustomizers(RelationalFieldHelper.discriminatorCustomisers);
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<Discriminator> discriminatorListView() {
		ListView<Discriminator> listview = new ListView<Discriminator>(Discriminator.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("id").collapse().rightAlign(),
				   new TableColumnConfig("uid").collapse(),
				   new TableColumnConfig("createdTs").collapse(),
				   new TableColumnConfig("modifiedTs").collapse(),

				   new TableColumnConfig("name").asc(),
				   new TableColumnConfig("selectable").centerAlign()				   
				   );
		   
		ActionHelper.setDefaultListViewActions(listview, "discriminatorEditorView");
		return listview;
	}
}
