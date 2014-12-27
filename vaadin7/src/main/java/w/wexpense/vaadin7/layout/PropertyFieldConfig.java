package w.wexpense.vaadin7.layout;

import w.wexpense.model.DBable;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;

public class PropertyFieldConfig<T> {

    private Object id;

    private AbstractLayout layout;

    protected float width;

    private Sizeable.Unit widthUnit;

    private float height;

    private Sizeable.Unit heightUnit;

    private Float expandRatio;
    
    private Boolean readOnly;

    private Converter<T, ?> converter;

    private Property.ValueChangeListener valueChangeListener;
    
    public PropertyFieldConfig(AbstractLayout layout, Object id) {
        super();
        this.id = id;
        this.layout = layout;
        this.readOnly = DBable.SYSTEM_PROPERTIES.contains(id);
    }

    public Object getId() {
        return id;
    }

    public AbstractLayout getLayout() {
        return layout;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void configure(BeanFieldGroup<T> fieldGroup) {
        configure(fieldGroup.getField(id));
    }

    public void configure(BeanFieldGroup<T> fieldGroup, T instance) {

    }

    protected void configure(Component c) {
        addToLayout(c);

        if (widthUnit != null) {
            c.setWidth(width, widthUnit);
        }
        if (heightUnit != null) {
            c.setHeight(height, heightUnit);
        }
        if (converter != null) {
            ((AbstractField<T>) c).setConverter(converter);
        }
        if (valueChangeListener != null) {
            ((AbstractField<T>) c).addValueChangeListener(valueChangeListener);
        }
        if (expandRatio != null && layout instanceof AbstractOrderedLayout) {
            ((AbstractOrderedLayout) layout).setExpandRatio(c, expandRatio);
        }
        
    }

    protected void addToLayout(Component c) {
        layout.addComponent(c);
    }

    public PropertyFieldConfig<T> width(float width, Sizeable.Unit widthUnit) {
        this.width = width;
        this.widthUnit = widthUnit;
        return this;
    }

    public PropertyFieldConfig<T> height(float height, Sizeable.Unit heightUnit) {
        this.height = height;
        this.heightUnit = heightUnit;
        return this;
    }

    public PropertyFieldConfig<T> readOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public PropertyFieldConfig<T> converter(Converter<T,?> converter) {
        this.converter = converter;
        return this;
    }
    
    public PropertyFieldConfig<T> valueChangeListener(Property.ValueChangeListener listener) {
        this.valueChangeListener = listener;
        return this;
    }
        
    public PropertyFieldConfig<T> expand(Float expandRatio) {
        this.expandRatio = expandRatio;
        return this;
    }
}
