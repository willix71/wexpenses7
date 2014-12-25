package w.wexpense.vaadin7.action;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.Action;

public abstract class ListViewAction extends Action implements Action.Listener {
	
	private static final long serialVersionUID = 1L;

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public ListViewAction(String caption) {
		super(caption);
	}
	
	public abstract boolean canHandle(Object target, Object sender);
}
