package w.wexpense.service;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import w.wexpense.service.instanciator.Initializor;

public class JpaRepoDaoService<T, ID extends Serializable> implements StorableService<T, ID> {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private JpaRepository<T, ID> dao;

	private Class<T> entityClass;

	private Initializor<T> initializors[];

	@SuppressWarnings("unchecked")
	public JpaRepoDaoService(Class<T> entityClass, JpaRepository<T, ID> dao) {
		this(entityClass, dao, new Initializor[] {});

	}

	// Generic and optional arguments do not get along very well....

	@SuppressWarnings("unchecked")
	public JpaRepoDaoService(Class<T> entityClass, JpaRepository<T, ID> dao, Initializor<T> initializor) {
		this(entityClass, dao, new Initializor[] { initializor });
	}

	@SuppressWarnings("unchecked")
	public JpaRepoDaoService(Class<T> entityClass, JpaRepository<T, ID> dao, Initializor<T> initializor1,
			Initializor<T> initializor2) {
		this(entityClass, dao, new Initializor[] { initializor1, initializor2 });
	}

	@SuppressWarnings("unchecked")
	public JpaRepoDaoService(Class<T> entityClass, JpaRepository<T, ID> dao, Initializor<T> initializor1,
			Initializor<T> initializor2, Initializor<T> initializor3) {
		this(entityClass, dao, new Initializor[] { initializor1, initializor2, initializor3 });
	}

	public JpaRepoDaoService(Class<T> entityClass, JpaRepository<T, ID> dao, Initializor<T> initializors[]) {
		this.entityClass = entityClass;
		this.dao = dao;
		this.initializors = initializors;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public JpaRepository<T, ID> getDao() {
		return dao;
	}

	@Override
	public T newInstance(Object... args) {
		try {
			T t = entityClass.newInstance();

			if (args != null && args.length > 0) {
				for (Initializor<T> i : initializors) {
					args = i.initialize(t, args);
					if (args == null || args.length == 0)
						break;
				}
			}

			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T load(ID id) {
		return dao.findOne(id);
	}

	@Override
	public List<T> loadAll() {
		return dao.findAll();
	}

	@Override
	public PagedContent<T> loadPage(int page, int size) {
		Page<T> result = dao.findAll(new PageRequest(page, size));
		return new PagedContent<>(result.getContent(), result.getTotalPages());
	}

	@Override
	@Transactional
	public T save(T entity) {
		return dao.save(entity);
	}

	@Override
	@Transactional
	public void delete(T entity) {
		dao.delete(entity);
	}

	@Override
	@Transactional
	public void delete(ID id) {
		dao.delete(id);
	}
}
