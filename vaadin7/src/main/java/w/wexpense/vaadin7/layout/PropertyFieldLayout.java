package w.wexpense.vaadin7.layout;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.AbstractLayout;

public class PropertyFieldLayout {

    private AbstractLayout layout;
    
    private List<PropertyFieldConfig> configs;
    
    private Object[] ids;

    public PropertyFieldLayout(AbstractLayout layout) {
        this.layout = layout;
    }
    
    public AbstractLayout getLayout() {
        return layout;
    }

    public void setPropertyFieldConfigs(List<PropertyFieldConfig> configs) {
        this.configs = configs;
     }

    public void addPropertyFieldConfig(PropertyFieldConfig config) {
        if (configs == null) {
            this.configs = new ArrayList<PropertyFieldConfig>();
        }
        this.configs.add(config);
     }
    
    public Iterable<PropertyFieldConfig> getPropertyFieldConfigs() {        
        return configs;
    }

    public Object[] getIds() {
        if (ids == null) {
            List<Object> lids = new ArrayList<Object>();
            for(PropertyFieldConfig config: getPropertyFieldConfigs()) {
                if (config.getId() != null) { lids.add(config.getId()); }            
            }
            this.ids = lids.toArray(new Object[lids.size()]);
        }
        return ids;
    }   
}
