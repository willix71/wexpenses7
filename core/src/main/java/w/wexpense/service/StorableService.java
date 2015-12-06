package w.wexpense.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface StorableService<T, ID extends Serializable> {

	Class<T> getEntityClass();
	
	T newInstance(Object ... args);

	T load(ID id);

	List<T> loadAll();
	
	PagedContent<T> loadPage(final int page, final int size, final String orderBy);
	
	@Transactional
	T save(T entity);

	@Transactional
	void delete(T entity);

	@Transactional
	void delete(ID id);

}