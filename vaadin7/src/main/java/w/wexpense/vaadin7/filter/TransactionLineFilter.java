package w.wexpense.vaadin7.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import w.wexpense.model.Account;
import w.wexpense.model.Discriminator;
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

public class TransactionLineFilter extends AbstractFilterView {
	
	@Autowired
	protected ContainerService persistenceService;
	
	Field<String> dateField = FilterHelper.getSelectFilter(this, 2, "date","createdTs","modifiedTs");
	Field<String> dateComparator = FilterHelper.getSelectFilter(this, 0, ">",">=","=","<=","<");
	DateField dateValue = FilterHelper.getDateFilter(this, null);
	Field<String> amountComparator = FilterHelper.getSelectFilter(this, 0, ">",">=","=","<=","<");
	TextField amountValue = FilterHelper.getTextFilter(this, "amount");
	TextField payeeValue = FilterHelper.getTextFilter(this, "payee");
	ComboBox accountValue;
	ComboBox discriminatorValue;
	Button clear;
	public TransactionLineFilter() {
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
				accountValue.setValue(null);
				fireFilterChangeEvent();
			}
		});
	}
	
	@PostConstruct
	public void postConstruct() {

		addComponent(clear);
	
		accountValue = FilterHelper.getEntityFilter(this, Account.class, persistenceService);
		addComponent(accountValue);

		discriminatorValue = FilterHelper.getEntityFilter(this, Discriminator.class, persistenceService);
		addComponent(discriminatorValue);
		
		addComponent(dateField);
		
		addComponent(dateComparator);
		
		addComponent(dateValue);

		addComponent(amountComparator);
		
		addComponent(amountValue);;
	
		addComponent(payeeValue);

	}
	
	@Override
	public Filter getFilter() {
		List<Filter> filters = new ArrayList<Filter>();
		
		if (accountValue.getValue() != null) {
			filters.add(new Compare.Equal("account", accountValue.getValue()));
		}
		if (discriminatorValue.getValue() != null) {
			filters.add(new Compare.Equal("discriminator", discriminatorValue.getValue()));
		}	
		if (dateValue.getValue() != null) {
			filters.add(FilterHelper.compare(dateField.getValue(), dateComparator.getValue(), dateValue.getValue()));
		}		
		if (StringUtils.hasText(amountValue.getValue())) {
			filters.add(FilterHelper.compare("amount", amountComparator.getValue(), new BigDecimal(amountValue.getValue())));
		}
		if (StringUtils.hasText(payeeValue.getValue())) {
			filters.add(new Like( "expense.payee.display", "%" + payeeValue.getValue() + "%", false));
		}				
		
		if (filters.isEmpty()) return null;
		else return new And(filters.toArray(new Filter[filters.size()]));
	}

}
