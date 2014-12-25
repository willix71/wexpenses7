package w.wexpense.vaadin7.event;

import com.vaadin.ui.Component;

public class EntityChangeEvent extends Component.Event {

	private static final long serialVersionUID = -3060677120276997236L;

	private Object id;
	private Object object;
	
	public EntityChangeEvent(Component source, Object id, Object object) {
		super(source);
		this.id = id;
		this.object = object;
	}
	
	public Object getId() {
		return id;
	}
	
	public Object getObject() {
		return object;
	}
}
