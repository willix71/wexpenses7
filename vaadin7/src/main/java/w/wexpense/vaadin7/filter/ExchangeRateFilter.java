package w.wexpense.vaadin7.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import w.wexpense.model.Currency;
import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.container.ContainerService;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Like;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class ExchangeRateFilter extends AbstractFilterView {
	
	@Autowired
	protected ContainerService persistenceService;
	
	Field<String> dateField = FilterHelper.getSelectFilter(this, 0, "date","createdTs","modifiedTs");
	Field<String> dateComparator = FilterHelper.getSelectFilter(this, 0, ">",">=","=","<=","<");
	DateField dateValue = FilterHelper.getDateFilter(this, null);
	Field<String> amountComparator = FilterHelper.getSelectFilter(this, 0, ">",">=","=","<=","<");
	TextField amountValue = FilterHelper.getTextFilter(this, "rate");
	TextField institutionValue = FilterHelper.getTextFilter(this, "institution");
	ComboBox fromCurrencyValue;
	ComboBox toCurrencyValue;
	Button clear;
	public ExchangeRateFilter() {
		UIHelper.rightAlign(this);
		
		clear = new Button("clear");
		clear.addClickListener(new ClickListener() {			
			@Override
			public void buttonClick(ClickEvent event) {
				dateField.setValue("date");
				dateComparator.setValue(">");
				dateValue.setValue(null);
				amountComparator.setValue(">");
				amountValue.setValue("");
				institutionValue.setValue("");
				fromCurrencyValue.setValue(null);
				toCurrencyValue.setValue(null);
				fireFilterChangeEvent();
			}
		});
	}
	
	@PostConstruct
	public void postConstruct() {

		addComponent(clear);
	
		addComponent(dateField);
		
		addComponent(dateComparator);
		
		addComponent(dateValue);

		addComponent(institutionValue);
		
		fromCurrencyValue = FilterHelper.getEntityFilter(this, Currency.class, persistenceService);
		fromCurrencyValue.setWidth(60, Unit.PIXELS);
		addComponent(fromCurrencyValue);

		toCurrencyValue = FilterHelper.getEntityFilter(this, Currency.class, persistenceService);
		toCurrencyValue.setWidth(60, Unit.PIXELS);
		addComponent(toCurrencyValue);

		addComponent(amountComparator);
		
		addComponent(amountValue);
	}
	
	@Override
	public Filter getFilter() {
		List<Filter> filters = new ArrayList<Filter>();
			
		if (dateValue.getValue() != null) {
			filters.add(FilterHelper.compare(dateField.getValue(), dateComparator.getValue(), dateValue.getValue()));
		}		
		if (StringUtils.hasText(amountValue.getValue())) {
			filters.add(FilterHelper.compare("rate", amountComparator.getValue(), new BigDecimal(amountValue.getValue())));
		}
		if (fromCurrencyValue.getValue() != null) {
			// why do need to use the nested code property but it works for the type (primary key is long instead of string?!?)
			filters.add(new Compare.Equal("fromCurrency.code", fromCurrencyValue.getValue()));
		}	
		if (toCurrencyValue.getValue() != null) {
			// why do need to use the nested code property but it works for the type (primary key is long instead of string?!?)
			filters.add(new Compare.Equal("toCurrency.code", toCurrencyValue.getValue()));
		}	
		if (StringUtils.hasText(institutionValue.getValue())) {
			filters.add(new Like( "institution.display", "%" + institutionValue.getValue() + "%", false));
		}		
	
		
		if (filters.isEmpty()) return null;
		else return new And(filters.toArray(new Filter[filters.size()]));
	}

}
