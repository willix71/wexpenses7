package w.wexpense.vaadin7.action;

import java.util.Collection;

import w.wexpense.vaadin7.container.OneToManyContainer;

import com.vaadin.ui.Table;

public class RemoveAction<T> extends ListViewAction {

	private static final long serialVersionUID = 1L;

	public RemoveAction() {
		super("remove");
	}

	@Override
	public void handleAction(final Object sender, final Object target) {
		if (target != null) {
			Table table = (Table) sender;
			OneToManyContainer<T> container = (OneToManyContainer<T>) table.getContainerDataSource();
			
			// OneToMany uses a multiselection table
			for (Object o : ((Collection<?>) table.getValue())) {
				container.removeBean((T) o);
			}
		}
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return target != null;
	}
}
