package w.wexpense.model;

public interface Duplicatable<T> extends Klonable<T> {

	T duplicate();
}
