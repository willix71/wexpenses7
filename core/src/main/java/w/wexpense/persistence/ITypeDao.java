package w.wexpense.persistence;

public interface ITypeDao<T> {
	T findByName(String uid);
}
