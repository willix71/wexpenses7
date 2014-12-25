package w.wexpense.vaadin7.action;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Table;

public class RefreshAction extends ListViewAction {
	
	private static final long serialVersionUID = 1L;

	public RefreshAction() {
		super("refresh");
	}
	
	@Override
	public void handleAction(Object sender, Object target) {
		if (sender instanceof Table) {
			com.vaadin.data.Container c = ((Table) sender).getContainerDataSource();
			if (c instanceof JPAContainer) {
				((JPAContainer<?>) c).refresh();	
			}
		}
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return sender instanceof Table && ((Table) sender).getContainerDataSource() instanceof JPAContainer;
	}

}
