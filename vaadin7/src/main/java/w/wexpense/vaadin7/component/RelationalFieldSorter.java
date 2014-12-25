package w.wexpense.vaadin7.component;

import java.util.Arrays;

public class RelationalFieldSorter extends RelationalFieldCustomizer {

	private Object propertyIds[];
	
	private boolean ascendings[];
	
	public RelationalFieldSorter(Class<?> clazz) {
		super(clazz);
	}

	public RelationalFieldSorter(Class<?> clazz, Object propertyId, boolean ascending) {
		super(clazz);
		this.propertyIds = new Object[]{propertyId};
		this.ascendings = new boolean[]{ascending};
	}

	public RelationalFieldSorter(Class<?> clazz, Object propertyIds[]) {
		super(clazz);
		this.propertyIds = propertyIds;
		this.ascendings = new boolean[propertyIds.length];
		Arrays.fill(this.ascendings, true);
	}

	public Object[] getPropertyIds() {
		return propertyIds;
	}

	public boolean[] getAscendings() {
		return ascendings;
	}
}
