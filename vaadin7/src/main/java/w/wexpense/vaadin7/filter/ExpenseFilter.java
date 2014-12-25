package w.wexpense.vaadin7.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import w.wexpense.model.Currency;
import w.wexpense.model.ExpenseType;
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

public class ExpenseFilter extends AbstractFilterView {
	
	@Autowired
	protected ContainerService persistenceService;
	
	Field<String> dateField = FilterHelper.getSelectFilter(this, 0, "date","createdTs","modifiedTs");
	Field<String> dateComparator = FilterHelper.getSelectFilter(this, 0, ">",">=","=","<=","<");
	DateField dateValue = FilterHelper.getDateFilter(this, null);
	Field<String> amountComparator = FilterHelper.getSelectFilter(this, 0, ">",">=","=","<=","<");
	TextField amountValue = FilterHelper.getTextFilter(this, "amount");
	TextField payeeValue = FilterHelper.getTextFilter(this, "payee");
	TextField refValue = FilterHelper.getTextFilter(this, "external reference");
	ComboBox currencyValue;
	ComboBox typeValue;
	Button clear;
	
	public ExpenseFilter() {
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
				payeeValue.setValue("");
				refValue.setValue("");
				currencyValue.setValue(null);
				typeValue.setValue(null);
				fireFilterChangeEvent();
			}
		});
	}
	
	@PostConstruct
	public void postConstruct() {

		addComponent(clear);
	
		typeValue = FilterHelper.getEntityFilter(this, ExpenseType.class, persistenceService);
		typeValue.setWidth(60, Unit.PIXELS);
		addComponent(typeValue);
		
		addComponent(dateField);
		
		addComponent(dateComparator);
		
		addComponent(dateValue);

		addComponent(amountComparator);
		
		addComponent(amountValue);
		
		currencyValue = FilterHelper.getEntityFilter(this, Currency.class, persistenceService);
		currencyValue.setWidth(60, Unit.PIXELS);
		addComponent(currencyValue);
	
		addComponent(payeeValue);
		
		addComponent(refValue);
	}
	
	@Override
	public Filter getFilter() {
		List<Filter> filters = new ArrayList<Filter>();
		
		if (typeValue.getValue() != null) {
			filters.add(new Compare.Equal("type", typeValue.getValue()));
		}	
		if (dateValue.getValue() != null) {
			filters.add(FilterHelper.compare(dateField.getValue(), dateComparator.getValue(), dateValue.getValue()));
		}		
		if (StringUtils.hasText(amountValue.getValue())) {
			filters.add(FilterHelper.compare("amount", amountComparator.getValue(), new BigDecimal(amountValue.getValue())));
		}
		if (currencyValue.getValue() != null) {
			// why do need to use the nested code property but it works for the type (primary key is long instead of string?!?)
			filters.add(new Compare.Equal("currency.code", currencyValue.getValue()));
		}	
		if (StringUtils.hasText(payeeValue.getValue())) {
			filters.add(new Like( "payee.display", "%" + payeeValue.getValue() + "%", false));
		}		
		if (StringUtils.hasText(refValue.getValue())) {
			filters.add(new Like( "externalReference", "%" + refValue.getValue() + "%", false));
		}		
		
		if (filters.isEmpty()) return null;
		else return new And(filters.toArray(new Filter[filters.size()]));
	}

}
