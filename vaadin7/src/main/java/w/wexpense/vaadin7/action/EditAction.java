package w.wexpense.vaadin7.action;

import java.io.Serializable;

import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.event.EntityChangeEvent;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

public class EditAction extends ListViewAction {

	private static final long serialVersionUID = 1L;

	private String editorName;

	public EditAction(String editorName) {
		super("edit");
		this.editorName = editorName;
	}	

	@Override
	public void handleAction(final Object sender, final Object target) {
		if (target != null) {
			final EditorView editor = ((WexUI) UI.getCurrent()).getBean(EditorView.class, editorName);
			editor.loadInstance(getInstanceId(sender,target));
			editor.addListener(new Component.Listener() {
				private static final long serialVersionUID = 8121179082149508635L;

				@Override
				public void componentEvent(Event event) {
					if (event instanceof EntityChangeEvent && event.getComponent() == editor && sender instanceof Table) {
				 		com.vaadin.data.Container c = ((Table) sender) .getContainerDataSource();
						if (c instanceof JPAContainer) {
							Object id = ((EntityChangeEvent) event).getId();
							if (id!=null) {
								((JPAContainer<?>) c).refreshItem(id);
							} else {
								((JPAContainer<?>) c).refresh();
							}
						} else if (c instanceof OneToManyContainer) {
							Object o = ((EntityChangeEvent) event).getObject();
							Object bean = getBean(sender, target, o);
							// first remove the bean (they have the same id) and add it back
							((OneToManyContainer) c).removeBean(bean);
							((OneToManyContainer) c).addBean(bean);
						}
					}
				}
			});
			
			UIHelper.displayWindow(editor); 
		}
	}

	public Object getBean(Object sender, Object target, Object bean) {
		return bean;
	}
	
	public Serializable getInstanceId(Object sender, Object target) {
		return (Serializable) target;
	}
	
	@Override
	public boolean canHandle(Object target, Object sender) {
		return target != null;
	}

}
