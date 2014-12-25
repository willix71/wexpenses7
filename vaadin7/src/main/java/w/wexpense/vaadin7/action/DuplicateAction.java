package w.wexpense.vaadin7.action;

import java.io.Serializable;

import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.event.EntityChangeEvent;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

public class DuplicateAction extends ListViewAction {
	
	private static final long serialVersionUID = 1L;

	private String editorName;
	
	public DuplicateAction(String editorName) {
		super("duplicate");
		this.editorName = editorName;
	}
	
	@Override
	public void handleAction(final Object sender, final Object target) {
		final EditorView editor = ((WexUI) UI.getCurrent()).getBean(EditorView.class, editorName);	
		editor.duplicate(getInstanceId(sender,target));
		
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

	public Serializable getInstanceId(Object sender, Object target) {
		return (Serializable) target;
	}
	
	@Override
	public boolean canHandle(Object target, Object sender) {
		return target != null;
	}
}
