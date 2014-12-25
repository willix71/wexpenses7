package w.wexpense.vaadin7.action;

import com.vaadin.ui.Table;

public class SelectAllAction extends ListViewAction {
	
	private static final long serialVersionUID = 1L;

	public SelectAllAction() {
		super("Select All");
	}
	
	@Override
	public void handleAction(Object sender, Object target) {
		if (sender instanceof Table) {
			Table t = (Table) sender;
			t.setValue(t.getItemIds());
		}
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return sender instanceof Table;
	}

}
