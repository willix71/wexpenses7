package w.wexpense.persistence;

public interface IUidableDao<T> {
	T findByUid(String uid);
}
