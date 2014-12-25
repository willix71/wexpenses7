package w.wexpense.vaadin7.action;

import w.wexpense.model.DBable;
import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.event.MutliSelectionChangeEvent;
import w.wexpense.vaadin7.view.MultiSelectorView;

import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class AddMultiSelectionAction<T extends DBable> extends ListViewAction {

	private static final long serialVersionUID = 1L;

	private String selectorName;
	private boolean resetMode;

	public AddMultiSelectionAction(String selectorName, boolean resetMode) {
		super(resetMode?"select":"add");
		this.resetMode = resetMode;
		this.selectorName = selectorName;
	}

	@Override
	public void handleAction(final Object sender, final Object target) {
		final OneToManyContainer<T> container = (OneToManyContainer<T>) ((Table) sender).getContainerDataSource();

		final MultiSelectorView<T> selector = ((WexUI) UI.getCurrent()).getBean(MultiSelectorView.class, selectorName);

		prepareSelector(selector, container, resetMode);
		
		selector.addListener(new Component.Listener() {
			private static final long serialVersionUID = 8121179082149508635L;

			@Override
			public void componentEvent(Event event) {
				if (event instanceof MutliSelectionChangeEvent && event.getComponent() == selector && sender instanceof Table) {
					if (resetMode) {
						container.resetBeans(((MutliSelectionChangeEvent) event).getBeans());
					} else {
						container.addBeans(((MutliSelectionChangeEvent) event).getBeans());
					}
				}
			}
		});

		Window w = UIHelper.displayModalWindow(selector);
		//w.setHeight(200,Unit.PIXELS);
		//w.setWidth(300, Unit.PIXELS);

	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return true;
	}

	public void prepareSelector(MultiSelectorView<T> selector, OneToManyContainer<T> container, boolean resetMode) {
		if (!container.isEmpty()) {
			if (resetMode) {
				selector.setValues(container.getBeans());
			} else {
				selector.setFilter(ActionHelper.excludeFilter(container.getBeans()));
			}
	   }
	}
}
