package w.wexpense.vaadin7.layout;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;

public class StaticFieldConfig<T> extends PropertyFieldConfig<T> {

    private Component component; 
    
    public StaticFieldConfig(AbstractLayout layout, Component component) {
        super(layout, null);
        this.component = component;
    }

    @Override
    public void configure(BeanFieldGroup<T> fieldGroup) {
        configure(component);
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
}
