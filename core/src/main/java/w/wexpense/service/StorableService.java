package w.wexpense.service;

import java.io.Serializable;

import org.springframework.transaction.annotation.Transactional;

public interface StorableService<T, ID extends Serializable> {

	Class<T> getEntityClass();
	
	T newInstance(Object ... args);

	T load(ID id);

	@Transactional
	T save(T entity);

	@Transactional
	void delete(T entity);

	@Transactional
	void delete(ID id);

}