package w.wexpense.persistence.dao;

public interface IDBableJpaDao<T> {
	T findByUid(String uid);
}
