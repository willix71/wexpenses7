package w.wexpense.service;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import w.wexpense.service.instanciator.Initializor;

public class DaoService<T, ID extends Serializable> implements StorableService<T, ID> {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private JpaRepository<T, ID> dao;
	
	private Class<T> entityClass;
	
	private Initializor<T> initializors[];
	
	@SuppressWarnings("unchecked")
	public DaoService(Class<T> entityClass, JpaRepository<T, ID> dao) {
		this(entityClass, dao, new Initializor[] {});

	}

	// Generic and optional arguments do not get along very well....
	
	@SuppressWarnings("unchecked")
	public DaoService(Class<T> entityClass, JpaRepository<T, ID> dao, Initializor<T> initializor) {
		this(entityClass, dao, new Initializor[] {initializor});
	}

	@SuppressWarnings("unchecked")
	public DaoService(Class<T> entityClass, JpaRepository<T, ID> dao, Initializor<T> initializor1, Initializor<T> initializor2) {
		this(entityClass, dao, new Initializor[] {initializor1, initializor2});
	}

	@SuppressWarnings("unchecked")
	public DaoService(Class<T> entityClass, JpaRepository<T, ID> dao, Initializor<T> initializor1, Initializor<T> initializor2, Initializor<T> initializor3) {
		this(entityClass, dao, new Initializor[] {initializor1, initializor2, initializor3});
	}
	
	public DaoService(Class<T> entityClass, JpaRepository<T, ID> dao, Initializor<T> initializors[]) {
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
   public T newInstance(Object ... args) {
		try {
			T t = entityClass.newInstance();
			
			if (args!=null && args.length>0) {
				for(Initializor<T> i: initializors) {
					args = i.initialize(t, args);
					if (args==null || args.length == 0) break;
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
