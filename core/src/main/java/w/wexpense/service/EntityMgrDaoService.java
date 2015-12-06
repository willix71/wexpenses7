package w.wexpense.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.service.instanciator.Initializor;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EntityMgrDaoService<T, ID extends Serializable> implements StorableService<T, ID> {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@PersistenceContext
	private EntityManager entityManager;

	private Class<T> entityClass;

	private Initializor<T> initializors[];

	@SuppressWarnings("unchecked")
	public EntityMgrDaoService(Class<T> entityClass) {
		this(entityClass, new Initializor[] {});
	}

	// Generic and optional arguments do not get along very well....

	@SuppressWarnings("unchecked")
	public EntityMgrDaoService(Class<T> entityClass, Initializor<T> initializor) {
		this(entityClass, new Initializor[] { initializor });
	}

	@SuppressWarnings("unchecked")
	public EntityMgrDaoService(Class<T> entityClass, Initializor<T> initializor1, Initializor<T> initializor2) {
		this(entityClass, new Initializor[] { initializor1, initializor2 });
	}

	@SuppressWarnings("unchecked")
	public EntityMgrDaoService(Class<T> entityClass, Initializor<T> initializor1, Initializor<T> initializor2,
			Initializor<T> initializor3) {
		this(entityClass, new Initializor[] { initializor1, initializor2, initializor3 });
	}

	public EntityMgrDaoService(Class<T> entityClass, Initializor<T> initializors[]) {
		this.entityClass = entityClass;
		this.initializors = initializors;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	@Override
	public T newInstance(Object... args) {
		T t;
		try {
			t = entityClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (args != null && args.length > 0) {
			for (Initializor<T> i : initializors) {
				args = i.initialize(t, args);
				if (args.length == 0)
					break;
			}
		}

		return t;
	}

	@Override
	public T load(ID id) {
		return entityManager.find(entityClass, id);
	}

	@Override
	public List<T> loadAll() {
		String entityName = entityClass.getSimpleName();
		return entityManager.createQuery("FROM " + entityName, entityClass).getResultList();
	}

	@Override
	public PagedContent<T> loadPage(int page, int size) {
		// first count the number of entities
		String entityName = entityClass.getSimpleName();
		int count = entityManager.createQuery("SELECT count(e) FROM " + entityName + " e", Number.class)
				.getSingleResult().intValue();

		int totalPages = count / size;
		if (count % size > 0)
			totalPages++;

		int fromIndex = page * size;
		if (fromIndex >= count) {
			return new PagedContent<T>(Collections.emptyList(), count, totalPages);
		} else {
			List<T> result = entityManager.createQuery("FROM " + entityName, entityClass).setFirstResult(fromIndex)
					.setMaxResults(size).getResultList();
			return new PagedContent<T>(result, count, totalPages);
		}
	}

	@Override
	public T save(T entity) {
		if (PersistenceUtils.getIdValue(entity) == null) {
			entityManager.persist(entity);
		} else {
			entity = entityManager.merge(entity);
		}

		return entity;
	}

	@Override
	public void delete(T entity) {
		entityManager.remove(entity);
	}

	@Override
	public void delete(ID id) {
		String entityName = entityClass.getSimpleName();
		String idName = PersistenceUtils.getIdName(entityClass);
		Query query = entityManager.createQuery("DELETE " + entityName + " WHERE " + idName + " = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}
}
