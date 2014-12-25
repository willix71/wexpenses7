package w.wexpense.vaadin7.view.model;

import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Consolidation;
import w.wexpense.model.TransactionLine;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.converter.StringToDateConverter;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.MultiSelectorView;

import com.vaadin.data.util.converter.Converter;

@Configuration
public class ConsolidationConfiguration {
	
	@Bean
	@Scope("prototype")
	public ListView<Consolidation> consolidationListView() {
		ListView<Consolidation> listview = new ListView<Consolidation>(Consolidation.class);
		listview.setColumnConfigs(
			   new TableColumnConfig("fullId").collapse().rightAlign(),
			   new TableColumnConfig("uid").collapse(),
			   new TableColumnConfig("createdTs").collapse(),
			   new TableColumnConfig("modifiedTs").collapse(),
			   
			   new TableColumnConfig("date").desc(),
			   new TableColumnConfig("institution").expand(1.0f),
			   new TableColumnConfig("openingBalance"),
			   new TableColumnConfig("closingBalance"),
			   new TableColumnConfig("deltaBalance", "delta")
			   );
		   
		ActionHelper.setDefaultListViewActions(listview, "consolidationEditorView");
		return listview;
	}
	
	@Bean
	@Scope("prototype")
	public MultiSelectorView<TransactionLine> consolidationTransactionLineSelectorView() {
		MultiSelectorView<TransactionLine> selector = new MultiSelectorView<TransactionLine>(TransactionLine.class);
		selector.setColumnConfigs(getTransactionLinesTableColumnConfig(new StringToDateConverter()));				
		return selector;
	}
	
	public static TableColumnConfig[] getTransactionLinesTableColumnConfig(Converter<String, Date> dateConverter) {

		return new TableColumnConfig[] {
		       new TableColumnConfig("fullId").collapse().rightAlign(),
			   new TableColumnConfig("uid").collapse(),
			   new TableColumnConfig("createdTs").collapse(),
			   new TableColumnConfig("modifiedTs").collapse(),

			   new TableColumnConfig("date").convert(dateConverter).asc(),
			   new TableColumnConfig("amount").rightAlign(),
			   new TableColumnConfig("exchangeRate").rightAlign(),
			   new TableColumnConfig("payee").expand(1.0f),			   
			   new TableColumnConfig("inValue").rightAlign(),
			   new TableColumnConfig("outValue").rightAlign(),
		};
 	}
}
