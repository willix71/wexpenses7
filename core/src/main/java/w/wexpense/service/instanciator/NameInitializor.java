package w.wexpense.service.instanciator;


public class NameInitializor<T> extends PropertyInitializor<T> {
	
	public NameInitializor(Class<T> entityClass) {
		super(entityClass, "setName", String.class);
   }
}
