package w.wexpense.vaadin7.view;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.vaadin7.event.SelectionChangeEvent;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class SelectorView<T> extends ListView<T> {

    protected Button buttons[] = new Button[] {
            new Button("Select", new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    select();
                }
            }), new Button("Cancel", new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    cancel();
                }
            })
        };
   
    public SelectorView(Class<T> entityClass) {
        super(entityClass);
    }
   
    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
       
        // add buttons
        Label shiftRight = new Label();
        HorizontalLayout buttonLayout = new HorizontalLayout(shiftRight);              
        buttonLayout.setExpandRatio(shiftRight, 1);
        buttonLayout.setWidth(100, Unit.PERCENTAGE);
        buttonLayout.addComponents(buttons);
        layout.addComponent(buttonLayout);
    }
   
    public void setValue(T value) {
        table.setValue(PersistenceUtils.getIdValue(value));
        
        // no need for this
        // table.markAsDirtyRecursive();
    }
       
    public void select() {
        LOGGER.debug("Selected entity {}", table.getValue());
       
        SelectionChangeEvent<T> event = new SelectionChangeEvent<T>(this);
        Serializable id = (Serializable) table.getValue();
        event.setId(id);
        event.setBean(getBean(id));
        
        fireEvent(event);
       
        close();
    }

    public void cancel() {
        close();
    }
    
    @SuppressWarnings("unchecked")
    protected T getBean(Serializable id) {
   	 if (id==null) return  null;
   	 EntityItem<T> item=  (EntityItem<T>) table.getItem(id);
   	 return item.getEntity();
    }
}

