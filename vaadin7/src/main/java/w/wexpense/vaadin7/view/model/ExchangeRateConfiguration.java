package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.vaadin.ui.AbstractField;

import w.wexpense.model.ExchangeRate;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.converter.StringToDoubleConverter;
import w.wexpense.vaadin7.filter.ExchangeRateFilter;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.SelectorView;

@Configuration
public class ExchangeRateConfiguration {

	@Autowired
	@Qualifier("exchangeRateService") 
	private StorableService<ExchangeRate, Long> exchangeRateService;
	
	@Bean
	@Scope("prototype")
	public EditorView<ExchangeRate, Long> exchangeRateEditorView() {
	    PropertyFieldLayout l = PropertyFieldHelper.getDBableFormPropertyFieldLayout("date","institution","fromCurrency","toCurrency","rate","fee","fixFee");
		EditorView<ExchangeRate, Long> editorview = new EditorView<ExchangeRate, Long>(exchangeRateService, l) {
		    private static final long serialVersionUID = 1L;
		    
			@Override
			public void initFields() {
			    super.initFields();
				((AbstractField) fieldGroup.getField("rate")).setConverter(new StringToDoubleConverter("0.00000##"));
				((AbstractField) fieldGroup.getField("fixFee")).setConverter(new StringToDoubleConverter("0.00"));
			};
		};
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<ExchangeRate> exchangeRateListView() {
		ListView<ExchangeRate> listview = new ListView<ExchangeRate>(ExchangeRate.class);
		listview.setColumnConfigs(getTableColumnConfig());
		listview.addFilterSource(getExchangeRateFilter());
		ActionHelper.setDefaultListViewActions(listview, "exchangeRateEditorView");
		return listview;
	}
	
	@Bean
	@Scope("prototype")
	public SelectorView<ExchangeRate> exchangeRateSelectorView() {
		SelectorView<ExchangeRate> selectorview = new SelectorView<ExchangeRate>(ExchangeRate.class);
		selectorview.setColumnConfigs(getTableColumnConfig());
		ActionHelper.setExchangeRateSelectorViewActions(selectorview, "exchangeRateEditorView");
		return selectorview;
	}
	
	@Bean
	@Scope("prototype")
	public ExchangeRateFilter getExchangeRateFilter() {
		return new ExchangeRateFilter();
	}
	
	private TableColumnConfig[] getTableColumnConfig() {
		return new TableColumnConfig[]{
			   new TableColumnConfig("fullId").collapse().rightAlign(),
			   new TableColumnConfig("uid").collapse(),
			   new TableColumnConfig("createdTs").collapse(),
			   new TableColumnConfig("modifiedTs").collapse(),

			   new TableColumnConfig("date").desc(),
			   new TableColumnConfig("institution").sortBy(".display").expand(1.0f), 
			   new TableColumnConfig("fromCurrency","Sell").centerAlign(),
			   new TableColumnConfig("toCurrency","Buy").centerAlign(),
			   new TableColumnConfig("strenghedRate","Rate").convert(new StringToDoubleConverter("0.00000")),
			   new TableColumnConfig("fee").convert(new StringToDoubleConverter("0.##%")),
			   new TableColumnConfig("fixFee","Fix Fee").convert(new StringToDoubleConverter("0.00")).rightAlign()
		};
	}
}
