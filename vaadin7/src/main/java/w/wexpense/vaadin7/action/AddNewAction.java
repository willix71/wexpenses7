package w.wexpense.vaadin7.action;

import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.event.EntityChangeEvent;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

public class AddNewAction extends ListViewAction {
	
	private static final long serialVersionUID = 1L;

	private String editorName;
	
	public AddNewAction(String editorName) {
		super("add");
		this.editorName = editorName;
	}
	
	@Override
	public void handleAction(final Object sender, final Object target) {
		@SuppressWarnings("rawtypes")
        final EditorView editor = ((WexUI) UI.getCurrent()).getBean(EditorView.class, editorName);	
		editor.newInstance();
		editor.addListener(new Component.Listener() {
			private static final long serialVersionUID = 8121179082149508635L;

			@Override
			public void componentEvent(Event event) {
				if (event instanceof EntityChangeEvent && event.getComponent() == editor && sender instanceof Table) {
					com.vaadin.data.Container c = ((Table) sender) .getContainerDataSource();
					if (c instanceof JPAContainer) {
						((JPAContainer<?>) c).refresh();
					}
				}
			}
		});
		
		UIHelper.displayWindow(editor); 
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return true;
	}
}
