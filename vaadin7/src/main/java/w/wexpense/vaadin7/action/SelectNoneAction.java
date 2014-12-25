package w.wexpense.vaadin7.action;

import com.vaadin.ui.Table;

public class SelectNoneAction extends ListViewAction {
	
	private static final long serialVersionUID = 1L;

	public SelectNoneAction() {
		super("Select None");
	}
	
	@Override
	public void handleAction(Object sender, Object target) {
		if (sender instanceof Table) {
			((Table) sender).setValue(null);
		}
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return sender instanceof Table;
	}

}
