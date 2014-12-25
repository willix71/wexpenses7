package w.wexpense.vaadin7.layout;

import w.wexpense.model.Codable;
import w.wexpense.model.DBable;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class PropertyFieldHelper {
    
    @SuppressWarnings("rawtypes")
    public static void addPropertyIds(PropertyFieldLayout l, Object ... ids) {
        for(Object id: ids) {
            l.addPropertyFieldConfig(new PropertyFieldConfig(l.getLayout(), id));
        }
    }
    
    public static HorizontalLayout getHorizontalLayout(boolean spacing, boolean margin) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(spacing);
        layout.setMargin(margin);
        return layout;
    }
    
    public static VerticalLayout getVerticalLayout(boolean spacing, boolean margin) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(spacing);
        layout.setMargin(margin);
        return layout;
    }
    
    public static FormLayout getFormLayout() {
        FormLayout layout = new FormLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        return layout;
    }
    
    public static GridLayout getGridLayout(int x, int y) {
        GridLayout layout = new GridLayout(x, y);
        layout.setSpacing(true);
        layout.setMargin(true);       
        return layout;
    }
    
    public static PropertyFieldLayout getFormPropertyFieldLayout(Object ... ids) {        
        PropertyFieldLayout pfl = new PropertyFieldLayout(getFormLayout());
        addPropertyIds(pfl, ids);
        return pfl;
    }
    
    @SuppressWarnings("rawtypes")
    public static PropertyFieldLayout getDBableFormPropertyFieldLayout(Object ... ids) {
        FormLayout layout = getFormLayout();
        PropertyFieldLayout pfl = new PropertyFieldLayout(layout);
        pfl.addPropertyFieldConfig(new PropertyFieldConfig<DBable>(layout, "fullId"));
        pfl.addPropertyFieldConfig(new PropertyFieldConfig<DBable>(layout, "uid").width(300, Unit.PIXELS));        
        addPropertyIds(pfl, ids);
        return pfl;
    }
    
    @SuppressWarnings("rawtypes")
    public static PropertyFieldLayout getCodableFormPropertyFieldLayout(Object ... ids) {
        FormLayout layout = getFormLayout();
        PropertyFieldLayout pfl = new PropertyFieldLayout(layout);
        pfl.addPropertyFieldConfig(new PropertyFieldConfig<Codable>(layout, "code") {
            @Override
            public void configure(BeanFieldGroup<Codable> fieldGroup, Codable instance) {
                readOnly(instance.getCode()!=null);
            }            
        });
        addPropertyIds(pfl, ids);
        return pfl;
    }
}
