package w.wexpense.vaadin7.event;

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

public class FieldCreationEvent extends Component.Event {

	private static final long serialVersionUID = -3060677120276997236L;

	private Object rowId;
	private Object colId;
	private Property<?> property;
	private Field<?> field;

	public FieldCreationEvent(Component source, Object rowId, Object colId, Property<?> property, Field<?> field) {
	   super(source);
	   this.rowId = rowId;
	   this.colId = colId;
	   this.property = property;
	   this.field = field;
   }

	public Object getRowId() {
		return rowId;
	}

	public Object getColId() {
		return colId;
	}

	public Property<?> getProperty() {
		return property;
	}

	public Field<?> getField() {
		return field;
	}

}
