package w.wexpense.vaadin7.view;

public class GenericView<T> extends WindowView {

    private static final long serialVersionUID = 1L;

    private Class<T> entityClass;

	public GenericView(Class<T> entityClass) {
		this.entityClass = entityClass;
		
		setViewName(entityClass.getSimpleName());
	}
		
	public Class<T> getEntityClass() {
		return entityClass;
	}

}