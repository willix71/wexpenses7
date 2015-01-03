package w.wexpense.test.utils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import w.wexpense.model.Codable;

public class PersistenceHelper {

   @PersistenceContext
   protected EntityManager entityManager;

   @Autowired
   protected PlatformTransactionManager transactionManager;

   public PersistenceHelper() {
      super();
   }
      
   public PersistenceHelper(EntityManager entityManager, PlatformTransactionManager transactionManager) {
      super();
      this.entityManager = entityManager;
      this.transactionManager = transactionManager;
   }


   public void persist(final Object... objs) {
      for (Object o : objs) {
         entityManager.persist(o);
      }
   }

   public void persistInTx(final Object... objs) {
      new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
         @Override
         protected void doInTransactionWithoutResult(TransactionStatus status) {
            for (Object o : objs) {
               entityManager.persist(o);
            }
         }
      });
   }

   public <T> T merge(final T t) {
      return entityManager.merge(t);
   }
   
   public <T> T mergeInTx(final T t) {
      return new TransactionTemplate(transactionManager).execute(new TransactionCallback<T>() {
         @Override
         public T doInTransaction(TransactionStatus status) {
            return entityManager.merge(t);
         }
      });
   }
   
   public int count(Class<?> clazz) {
      Query q = entityManager.createQuery("select count(*) from " + clazz.getSimpleName());
      return ((Number) q.getSingleResult()).intValue();
   }
   
   public <T> T get(Class<T> clazz, Object id) {
      return entityManager.find(clazz, id);
   }
   
   public <T extends Codable<T>> T getByCode(Class<T> clazz, String code) {
      return entityManager.find(clazz, code);
   }
   
   @SuppressWarnings("unchecked")
   public <T> T getByUid(Class<T> clazz, String uid) {
      Query q = entityManager.createQuery("from " + clazz.getSimpleName() + " x where x.uid=:uid");
      return (T) q.setParameter("uid", uid).getSingleResult();
   }

   @SuppressWarnings("unchecked")
   public <T> T getByName(Class<T> clazz, String name) {
      Query q = entityManager.createQuery("from " + clazz.getSimpleName() + " x where x.name=:name");
      return (T) q.setParameter("name", name).getSingleResult();
   }
   
   @SuppressWarnings("unchecked")  
   public <T> List<T> getAll(Class<T> clazz) {
      return (List<T>) entityManager.createQuery("from " + clazz.getSimpleName()).getResultList();
   }
}
