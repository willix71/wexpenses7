package w.wexpense.vaadin7.component;

import com.vaadin.data.Container.Filter;

public class RelationalFieldFilter extends RelationalFieldCustomizer {
	
	public enum Associater { OR, AND };
	
	private Filter filter;

	private Associater associater;
	
	public RelationalFieldFilter(Class<?> clazz, Filter filter) {
		super(clazz);
		this.filter = filter;
		this.associater = null;
	}	
	
	public RelationalFieldFilter(Class<?> clazz, Filter filter, Associater associater) {
		super(clazz);
		this.filter = filter;
		this.associater = associater;
	}
	
	public Filter getFilter() {
		return filter;
	}

	public Associater getAssociater() {
		return associater;
	}
}
