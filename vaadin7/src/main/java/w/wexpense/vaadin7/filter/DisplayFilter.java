package w.wexpense.vaadin7.filter;

import w.wexpense.vaadin7.UIHelper;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Like;
import com.vaadin.ui.TextField;

public class DisplayFilter extends AbstractFilterView implements Property.ValueChangeListener {
			
	private TextField displayCriteria;
	
	public DisplayFilter() {
		UIHelper.rightAlign(this);
		
		displayCriteria = FilterHelper.getTextFilter(this, "filter");
		
		addComponent(displayCriteria);
	}

	@Override
	public Filter getFilter() {
		return new Like("display", "%" + displayCriteria.getValue() + "%", false);
	}

	@Override
	public void valueChange(Property.ValueChangeEvent event) {
		fireFilterChangeEvent();
	}
}
