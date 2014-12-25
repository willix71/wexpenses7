package w.wexpense.vaadin7.filter;

import w.wexpense.vaadin7.component.WexDateField;
import w.wexpense.vaadin7.container.ContainerService;

import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

public class FilterHelper {

	public static Filter and(Filter f1, Filter f2) {
		if (f1 != null) {
			if (f2 != null) {
				return new And(f1,f2);
			} else {
				return f1;
			}
		} else {
			if (f2 != null) {
				return f2;
			} else {
				return null;
			}
		}
	}
	
	public static NativeSelect getSelectFilter(final AbstractFilterView view, String ... values) {
		IndexedContainer c = new IndexedContainer();
		for(String s: values) {
			c.addItem(s);
		}

		NativeSelect select = new NativeSelect("", c);
		select.setCaption(null);
		select.setImmediate(true);
		select.addValueChangeListener(new Property.ValueChangeListener() {			
			@Override
			public void valueChange(ValueChangeEvent event) {
				view.fireFilterChangeEvent();
			}
		});
		return select;	
	}
	
	public static Field<String> getSelectFilter(final AbstractFilterView view, int defaultValueIndex, String ... values) {
		NativeSelect select = getSelectFilter(view, values);
		select.setValue(values[defaultValueIndex]);
		select.setNullSelectionAllowed(false);
		return (Field) select;
	}

	public static DateField getDateFilter(final AbstractFilterView view, String caption) {
		DateField dateFilter = new WexDateField();
		dateFilter.setImmediate(true);
		dateFilter.setDateFormat("dd.MM.yyyy");
		dateFilter.setResolution(Resolution.DAY);
		dateFilter.addValueChangeListener(new Property.ValueChangeListener() {			
			@Override
			public void valueChange(ValueChangeEvent event) {
				view.fireFilterChangeEvent();
			}
		});
		return dateFilter;
	}
	
	public static TextField getTextFilter(final AbstractFilterView view, String caption) {
		final TextField txtfield = new TextField();
		txtfield.setInputPrompt(caption);
		txtfield.setTextChangeEventMode(TextChangeEventMode.LAZY);
		txtfield.addTextChangeListener(new TextChangeListener() {			
			@Override
			public void textChange(TextChangeEvent event) {
				txtfield.setValue( event.getText() );
				view.fireFilterChangeEvent();
			}
		});
		return txtfield;
	}

	public static ComboBox getEntityFilter(final AbstractFilterView view, Class<?> type, ContainerService persistenceService) {
			ComboBox comboBox = new ComboBox();
			comboBox.setImmediate(true);
			comboBox.setContainerDataSource(persistenceService.getContainer(type));
			comboBox.setConverter(new SingleSelectConverter(comboBox));
			comboBox.setItemCaptionMode(NativeSelect.ItemCaptionMode.ITEM);
			comboBox.setFilteringMode(FilteringMode.CONTAINS);
			comboBox.addValueChangeListener(new Property.ValueChangeListener() {			
				@Override
				public void valueChange(ValueChangeEvent event) {
					view.fireFilterChangeEvent();
				}
			});
			return comboBox;
	}
	
	public static Filter compare(String propertyId, String comparator, Object value) {
		if ("=".equals(comparator)) {
			return new Compare.Equal(propertyId, value);
		} else if (">=".equals(comparator)) {
			return new Compare.GreaterOrEqual(propertyId, value);						
		} else if ("<=".equals(comparator)) {						
			return new Compare.LessOrEqual(propertyId, value);						
		} else if (">".equals(comparator)) {
			return new Compare.Greater(propertyId, value);												
		} else if ("<".equals(comparator)) {
			return new Compare.Less(propertyId, value);												
		} else {
			return null;
		}
	}
}
