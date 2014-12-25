package w.wexpense.vaadin7.event;

import java.util.ArrayList;
import java.util.Collection;

import w.wexpense.persistence.PersistenceUtils;

import com.vaadin.ui.Component;

public class MutliSelectionChangeEvent<T> extends Component.Event {

    private static final long serialVersionUID = -3060677120276997236L;

    private Collection<Object> ids = new ArrayList<Object>();
    private Collection<T> beans = new ArrayList<T>();;

    public MutliSelectionChangeEvent(Component source) {
        super(source);
    }

    public Collection<Object> getIds() {
        return ids;
    }

    public void setIds(Collection<Object> ids) {
        this.ids = ids;
    }

    public Collection<T> getBeans() {
        return beans;
    }

    public void setBeans(Collection<T> beans) {
        this.beans = beans;
    }


    public void add(Object id, T bean) {
        beans.add(bean);
       
        if (id == null) {
            id = PersistenceUtils.getIdValue(bean);
        }
        ids.add(id);
    }
}


