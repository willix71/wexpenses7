package w.wexpense.vaadin7.event;

import com.vaadin.data.Container.Filter;
import com.vaadin.ui.Component;

public class FilterChangeEvent extends Component.Event {

	private static final long serialVersionUID = -3060677120276997236L;

	private Filter filter;

	public FilterChangeEvent(Component arg0, Filter filter) {
	   super(arg0);
	   this.filter = filter;
   }

	public Filter getFilter() {
		return filter;
	}
}
