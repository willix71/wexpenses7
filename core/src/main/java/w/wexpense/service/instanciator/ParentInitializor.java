package w.wexpense.service.instanciator;

import java.util.Arrays;

import w.wexpense.model.Parentable;

public class ParentInitializor<T extends Parentable<T>> implements Initializor<T> {

	private Class<T> entityClass;
	
	public ParentInitializor(Class<T> entityClass) {
		this.entityClass = entityClass;
	}	
	
   @Override
   public Object[] initialize(T t, Object[] args) {
   	if (args==null || args.length == 0 || !entityClass.isInstance(args[0])) return args;
   	
   	@SuppressWarnings("unchecked")
   	T parent = (T) args[0];
   	t.setParent(parent);
   	return Arrays.copyOfRange(args, 1, args.length);
   }
}
