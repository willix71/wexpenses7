package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.City;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

@Configuration
public class CityConfiguration {

	@Autowired
	@Qualifier("cityService")
	private StorableService<City, Long> cityService;
	
	@Bean
	@Scope("prototype")
	public EditorView<City, Long> cityEditorView() {
	    PropertyFieldLayout l = PropertyFieldHelper.getDBableFormPropertyFieldLayout("zip","name","country");
		EditorView<City, Long> editorview = new EditorView<City, Long>(cityService, l);
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<City> cityListView() {
		ListView<City> listview = new ListView<City>(City.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("fullId").collapse().rightAlign(),
				   new TableColumnConfig("uid").collapse(),
				   new TableColumnConfig("createdTs").collapse(),
				   new TableColumnConfig("modifiedTs").collapse(),

				   new TableColumnConfig("zip").rightAlign(),
				   new TableColumnConfig("name").asc(),
				   new TableColumnConfig("country.code")
				   );
		   
		ActionHelper.setDefaultListViewActions(listview, "cityEditorView");
		return listview;
	}
}
