package w.wexpense.service.instanciator;

public interface Initializor<T> {

	Object[] initialize(T t, Object[] args);
}
