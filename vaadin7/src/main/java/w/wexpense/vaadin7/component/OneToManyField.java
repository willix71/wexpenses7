package w.wexpense.vaadin7.component;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.container.ContainerService;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.event.FieldCreationEvent;
import w.wexpense.vaadin7.support.TableColumnConfig;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;

public class OneToManyField<T> extends CustomField<Collection<T>> {

	private static final long serialVersionUID = 1L;

	@Autowired
	protected ContainerService persistenceService;

	private OneToManyContainer<T> container;
	private Table table;

	private ActionHandler actionHandler;

	public OneToManyField(Class<T> entityClass, ContainerService persistenceService, TableColumnConfig... columnConfigs) {
		this(entityClass, columnConfigs);
		this.persistenceService = persistenceService;
	}

	public OneToManyField(Class<T> entityClass, TableColumnConfig... columnConfigs) {
		container = new OneToManyContainer<T>(entityClass);

		table = new Table(null, container) {
			@Override
			protected void bindPropertyToField(Object rowId, Object colId, Property property, Field field) {
				super.bindPropertyToField(rowId, colId, property, field);
				initField(rowId, colId, property, field);
			}
		};

		table.setBuffered(false);
		table.setImmediate(true);

		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setMultiSelectMode(MultiSelectMode.SIMPLE);

		table.setColumnCollapsingAllowed(true);
		TableColumnConfig.configure(table, columnConfigs);

		table.setWidth(100, Unit.PERCENTAGE);
	}
	
	@Override
	protected Component initContent() {
		if (persistenceService != null) {
			table.setTableFieldFactory(new RelationalFieldFactory(persistenceService));
		}
		return table;
	}

	
   @Override
	protected void setInternalValue(Collection<T> newValue) {
		if (newValue == null) {
			newValue = new ArrayList<T>();
			getPropertyDataSource().setValue(newValue);
		}

		super.setInternalValue(newValue);

		container.setBeans(newValue);

		if (table != null) {
			table.markAsDirtyRecursive();
		}
	}

	protected void initField(Object rowId, Object colId, Property<?> property, Field<?> field) {
		fireEvent(new FieldCreationEvent(this, rowId, colId, property, field));
	}
	
	@Override	
	public Class<? extends Collection<T>> getType() {
		return (Class<? extends Collection<T>>) Collection.class;
	}

	public ActionHandler getActionHandler() {
		return  this.actionHandler;
	}
	
	public void setActionHandler(ActionHandler actionHandler) {
		this.actionHandler = actionHandler;
		this.actionHandler.setTable(table);
	}
	
	public void setPageLength(int length) {
		table.setPageLength(length);
	}

	public void setEditable(boolean editable) {
		table.setEditable(editable);
	}
	
	public void addFooterListener(Container.ItemSetChangeListener listener) {		
		table.setFooterVisible(true);
		container.addItemSetChangeListener(listener);
	}
	
	public void setFooter(Object propertyId, String footer) {
		table.setColumnFooter(propertyId, footer);
	}
	
	public void clear() {
	    table.removeAllItems();
	}
	public void itemChange() {
		container.fireItemSetChange();
	}

}