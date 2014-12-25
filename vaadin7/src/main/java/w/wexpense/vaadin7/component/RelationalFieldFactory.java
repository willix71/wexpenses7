package w.wexpense.vaadin7.component;

import java.util.ArrayList;
import java.util.List;

import w.wexpense.model.Codable;
import w.wexpense.model.DBable;
import w.wexpense.model.ExchangeRate;
import w.wexpense.vaadin7.container.ContainerService;

import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Or;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;

public class RelationalFieldFactory extends SimpleFieldFactory {

	private static final long serialVersionUID = -2122739273213720235L;
	
	protected ContainerService persistenceService;
	
	private RelationalFieldCustomizer[] customizers;
	
	public RelationalFieldFactory(ContainerService persistenceService, RelationalFieldCustomizer ... customizers) {
		super();
		this.persistenceService = persistenceService;
		this.customizers = customizers;
	}

	@Override
	protected Field<?> createField(Class<?> type) {
		// Null typed properties can not be edited
		if (type == null) {
			return null;
		}
		
		Field<?> f = createRelationalField(type);
		if (f == null) {
			f = createSimpleField(type);
		}
		return f;
	}


	protected Field<?> createRelationalField(Class<?> type) {
		if (ExchangeRate.class.isAssignableFrom(type)) {
			return new ExchangeRateField();
		}
		if (Codable.class.isAssignableFrom(type)) {
			return createManyToOneField(type);
		}
		if (DBable.class.isAssignableFrom(type)) {
			return createEditableManyToOneField(type);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Field<?> createManyToOneField(Class<?> type) {
		ComboBox comboBox = new ComboBox();
		comboBox.setImmediate(true);
		comboBox.setContainerDataSource(getContainer(type));
		comboBox.setConverter(new SingleSelectConverter(comboBox));
		comboBox.setItemCaptionMode(NativeSelect.ItemCaptionMode.ITEM);
		comboBox.setFilteringMode(FilteringMode.CONTAINS);
		comboBox.setSizeFull();
		return comboBox;	
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
   protected Field<?> createEditableManyToOneField(Class<?> type) {
		return new WexComboBox(type, getContainer(type));		
	}
	
	protected Container getContainer(Class<?> type) {
		List<Filter> interfaceFilters = new ArrayList<Filter>();
		RelationalFieldFilter classFilter = null;
		Object propertyIds[] = null;
		boolean ascendings[] = null;
		
		if (customizers != null) {
			for(RelationalFieldCustomizer customizer: customizers) {
				if (customizer.getType().equals(type)) {
					if (customizer instanceof RelationalFieldFilter) {
						classFilter = (RelationalFieldFilter) customizer;
					} else if (customizer instanceof RelationalFieldSorter) {
						RelationalFieldSorter sorter = (RelationalFieldSorter) customizer;
						propertyIds = sorter.getPropertyIds();
						ascendings = sorter.getAscendings();
					}
				} else if (customizer.getType().isAssignableFrom(type)) {
					interfaceFilters.add(((RelationalFieldFilter) customizer).getFilter());
				}
			}
		}
		
		Filter filter = null;
		if (classFilter != null) {
			if (classFilter.getAssociater() == null) {
				filter = classFilter.getFilter();
			}else {
				interfaceFilters.add(classFilter.getFilter());
				if (RelationalFieldFilter.Associater.AND==classFilter.getAssociater()) {
					filter = new And(interfaceFilters.toArray(new Filter[interfaceFilters.size()]));
				} else if (RelationalFieldFilter.Associater.OR==classFilter.getAssociater()) {
					filter = new Or(interfaceFilters.toArray(new Filter[interfaceFilters.size()]));
				}
			}
		} else if (interfaceFilters.size()==1) {
			filter = interfaceFilters.get(0);
		} else if (interfaceFilters.size()>1) {
			filter = new And(interfaceFilters.toArray(new Filter[interfaceFilters.size()]));
		}
		
		return persistenceService.getContainer(type, filter, propertyIds, ascendings);
	}
}