package w.wexpense.vaadin7.filter;

import java.util.ArrayList;
import java.util.Collection;

import w.wexpense.vaadin7.event.FilterChangeEvent;
import w.wexpense.vaadin7.event.FilterChangeListener;

import com.vaadin.data.Container.Filter;
import com.vaadin.ui.HorizontalLayout;

public abstract class AbstractFilterView extends HorizontalLayout implements FilterView {

	private Collection<FilterChangeListener> filterChangeListeners;

	public abstract Filter getFilter();
	
	protected void fireFilterChangeEvent() {
      if (filterChangeListeners != null && filterChangeListeners.size() > 0) {
      	FilterChangeEvent event = new FilterChangeEvent(this, getFilter());

         final Object[] l = filterChangeListeners.toArray();
         for (int i = 0; i < l.length; i++) {
             ((FilterChangeListener) l[i]).filterChange(event);
         }
     }
	}
	
	@Override
   public void addFilterChangeListener(FilterChangeListener listener) {
	   if (filterChangeListeners == null) {
	   	filterChangeListeners = new ArrayList<FilterChangeListener>();
	   }
	   filterChangeListeners.add(listener);
   }


	@Override
   public void removeFilterChangeListener(FilterChangeListener listener) {
		if (filterChangeListeners != null) {
			filterChangeListeners.remove(listener);
		}
   }
}
