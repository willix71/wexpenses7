package w.wexpense.vaadin7.action;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Listener;

public abstract class SimpleAction extends Action implements Listener {

	public SimpleAction(String caption) {
		super(caption);
	}
}
