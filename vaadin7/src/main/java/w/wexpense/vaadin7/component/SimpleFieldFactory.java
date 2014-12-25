package w.wexpense.vaadin7.component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;

public class SimpleFieldFactory implements FieldGroupFieldFactory, TableFieldFactory {

	private static final long serialVersionUID = -2122739273213720235L;
	
	public SimpleFieldFactory() {}

	@Override
	public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
		Item item = container.getItem(itemId);

		Class<?> type = item.getItemProperty(propertyId).getType();

		return createField(type, Field.class);
	}
	
   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
		Field field = createField(dataType);		
		
		field.setWidth(100, Sizeable.Unit.PERCENTAGE);

		return (T) field;
	}
	
	protected Field<?> createField(Class<?> type) {
		// Null typed properties can not be edited
		if (type == null) {
			return null;
		}
		return createSimpleField(type);
	}
	
	protected Field<?> createSimpleField(Class<?> type) {
		// Date field
		if (Date.class.isAssignableFrom(type)) {
			return new WexDateField();
		}

		// Boolean field
		if (Boolean.class.isAssignableFrom(type)) {
			return new CheckBox();
		}

		// Enum field
		if (type.isEnum()) {
			ComboBox select = new ComboBox();
			select.setFilteringMode(FilteringMode.STARTSWITH);
			List<?> asList = Arrays.asList(type.getEnumConstants());
			for (Object object : asList) {
				select.addItem(object);
			}
			return select;
		}


		TextField field = new TextField();
		field.setNullRepresentation("");
		return field;
	}
}
