package w.wexpense.rest.utils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DButils {

	private static final Logger LOGGER = LoggerFactory.getLogger(DButils.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	public Long getVersion(Class<?> entityClass, Object id) {
		return getVersion(entityClass.getSimpleName(), id);
	}
	
	public Long getVersion(String entityClass, Object id) {
		try {
			TypedQuery<Long> query = entityManager.createQuery("SELECT version FROM " + entityClass + " WHERE id = :id", Long.class);
			query.setParameter("id", id);		
			return query.getSingleResult();
		} catch(NoResultException nre) {
			return null; // to be consistent with JpaRepoDaoService
		} catch(RuntimeException unexpected) {
			LOGGER.error("Failed to retrieve version for [{}] id:{}",entityClass,id);
			throw unexpected;
		}
	}
}
