package w.wexpense.vaadin7.component;

public class RelationalFieldCustomizer {

	private Class<?> clazz;

	public RelationalFieldCustomizer(Class<?> clazz) {
		this.clazz = clazz;
	}	
	
	public Class<?> getType() {
		return clazz;
	}
}
