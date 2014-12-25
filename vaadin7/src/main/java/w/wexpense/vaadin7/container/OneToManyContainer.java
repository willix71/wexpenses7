package w.wexpense.vaadin7.container;

import java.util.Collection;
import java.util.Collections;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;

public class OneToManyContainer<BEANTYPE> extends BeanItemContainer<BEANTYPE> {

    private static final long serialVersionUID = 1L;
    
    private Collection<BEANTYPE> beans;

	public OneToManyContainer(Class<? super BEANTYPE> type) throws IllegalArgumentException {
		super(type);
	}

	public Collection<BEANTYPE> getBeans() {
		return Collections.unmodifiableCollection(beans);
	}
	
	public void setBeans(Collection<BEANTYPE> ts) {
	    if (beans != null) {
	        removeAllItems();
	    }
	    
		beans = ts;
		
		addAll(ts);
	}
	
	public void resetBeans(Collection<BEANTYPE> ts) {
        removeAllItems();
		addBeans(ts);
	}
		
	public void addBeans(Collection<BEANTYPE> ts) {
		beans.addAll(ts);
		
		addAll(ts);
	}

	public BeanItem<BEANTYPE> addBean(BEANTYPE t) {		
		beans.add(t);
		
		return super.addBean(t);
	}


    @Override
    public boolean removeAllItems() {
        beans.clear();
        
        return super.removeAllItems();
    }
	
	public void removeBean(BEANTYPE t) {	
		// first remove from OneToMany beans because the next call will fire the itemSetChange
		beans.remove(t);

		removeItem(t);		
	}
	
	public boolean isEmpty() {
		return beans.isEmpty();
	}
	
	public void fireItemSetChange() {
      super.fireItemSetChange();
  }
}
