package w.wexpense.vaadin7.filter;

import w.wexpense.vaadin7.event.FilterChangeListener;

import com.vaadin.ui.Component;

public interface FilterView extends Component {

	public void addFilterChangeListener(FilterChangeListener listener);

   public void removeFilterChangeListener(FilterChangeListener listener);
}
