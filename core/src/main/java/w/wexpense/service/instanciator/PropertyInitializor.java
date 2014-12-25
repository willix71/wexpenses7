package w.wexpense.service.instanciator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class PropertyInitializor<T> implements Initializor<T> {

	private Method setter;

	private Class<?> type;

	public PropertyInitializor(Class<T> entityClass, String methodName, Class<?> type) {
		this.type = type;
		try {
			setter = entityClass.getMethod(methodName, type);
		} catch (NoSuchMethodException e) {
			// TODO loggging
			setter = null;
		}
	}

	@Override
	public Object[] initialize(T t, Object[] args) {
		if (setter != null && args != null && args.length > 0 && type.isInstance(args[0])) {
			try {
				setter.invoke(t, args[0]);

				return Arrays.copyOfRange(args, 1, args.length);
			} catch (IllegalAccessException | InvocationTargetException e) {
				// TODO loggging
			}
		}
		return args;
	}
}
