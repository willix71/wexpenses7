package w.wexpense.vaadin7.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import w.wexpense.vaadin7.UIHelper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Like;
import com.vaadin.ui.TextField;

public class PayeeFilter extends AbstractFilterView {
			
	private TextField displayCriteria = FilterHelper.getTextFilter(this, "payee");

	private TextField externalRefCriteria = FilterHelper.getTextFilter(this, "externalRef");
	
	private TextField bankDetailsDisplayCriteria = FilterHelper.getTextFilter(this, "bank details");
	
	private TextField bankExternalRefDisplayCriteria = FilterHelper.getTextFilter(this, "bank externalRef");

	public PayeeFilter() {
		UIHelper.rightAlign(this);
		
		addComponent(displayCriteria);
		
		addComponent(externalRefCriteria);
		
		addComponent(bankDetailsDisplayCriteria);
		
		addComponent(bankExternalRefDisplayCriteria);
	}

	@Override
	public Filter getFilter() {
		List<Filter> filters = new ArrayList<Filter>();
		
		if (StringUtils.hasText(bankExternalRefDisplayCriteria.getValue())) {
			filters.add(new Like("bankDetails.externalReference", "%" + bankExternalRefDisplayCriteria.getValue() + "%", false));
		}	
		
		if (StringUtils.hasText(bankDetailsDisplayCriteria.getValue())) {
			filters.add(new Like("bankDetails.display", "%" + bankDetailsDisplayCriteria.getValue() + "%", false));
		}		
		
		if (StringUtils.hasText(externalRefCriteria.getValue())) {
			filters.add(new Like("externalReference", "%" + externalRefCriteria.getValue() + "%", false));
		}
		
		if (StringUtils.hasText(displayCriteria.getValue())) {
			filters.add(new Like("display", "%" + displayCriteria.getValue() + "%", false));
		}		
		
		if (filters.isEmpty()) return null;
		else return new And(filters.toArray(new Filter[filters.size()]));
	}
}
