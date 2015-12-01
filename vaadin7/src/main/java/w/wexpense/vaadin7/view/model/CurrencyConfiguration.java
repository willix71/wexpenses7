package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Currency;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

@Configuration
public class CurrencyConfiguration {

	@Autowired
	@Qualifier("currencyService")
	private StorableService<Currency, String> currencyService;
	
	@Bean
	@Scope("prototype")
	public EditorView<Currency, String> currencyEditorView() {
	    PropertyFieldLayout l = PropertyFieldHelper.getFormPropertyFieldLayout("code","name","strengh");
		EditorView<Currency, String> editorview = new EditorView<Currency, String>(currencyService, l);
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<Currency> currencyListView() {
		ListView<Currency> listview = new ListView<Currency>(Currency.class);
		listview.setColumnConfigs(
				   new TableColumnConfig("code").asc(),
				   new TableColumnConfig("name").asc(),
				   new TableColumnConfig("strengh")
				   );
		   
		ActionHelper.setDefaultListViewActions(listview, "currencyEditorView");
		return listview;
	}
}
