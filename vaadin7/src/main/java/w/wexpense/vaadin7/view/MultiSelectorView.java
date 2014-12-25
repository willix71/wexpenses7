package w.wexpense.vaadin7.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.SelectAllAction;
import w.wexpense.vaadin7.action.SelectNoneAction;
import w.wexpense.vaadin7.event.MutliSelectionChangeEvent;

import com.vaadin.shared.ui.MultiSelectMode;

public class MultiSelectorView<T> extends SelectorView<T> {
   
    public MultiSelectorView(Class<T> entityClass) {
        super(entityClass);
        setActionHandler(new ActionHandler(new SelectAllAction(), new SelectNoneAction()));
    }
   
    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();
       
        table.setMultiSelect(true);
        table.setMultiSelectMode(MultiSelectMode.SIMPLE);
    }
   
    public void setValues(Collection<T> values) {
        List<Object> ids = new ArrayList<>();
        for(T value: values) {
            ids.add(PersistenceUtils.getIdValue(value));
        }
        table.setValue(ids);
        
        // no need for this
        // table.markAsDirtyRecursive();
    }
       
    @Override
    public void select() {
        LOGGER.debug("Selected entities {}", table.getValue());
       
        MutliSelectionChangeEvent<T> event = new MutliSelectionChangeEvent<T>(this);
        for(Object id: (Collection<?>) table.getValue()) {
            event.add(id, getBean((Serializable) id));
        }
       
        fireEvent(event);
       
        close();
    }
}

