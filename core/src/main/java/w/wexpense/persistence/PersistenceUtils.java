package w.wexpense.persistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.utils.BeanUtils;


public class PersistenceUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceUtils.class);
	
	/**
	 * This method creates a new instance of the class type.
	 * 
	 * @return a new instance of the class type
	 * @throws RuntimeException
	 */
	public static <T> T newInstance(Class<T> clazz) {
		T newInstance;
		try {
			newInstance = clazz.newInstance();
			return newInstance;
		} catch (InstantiationException  | IllegalAccessException e) {
			LOGGER.warn("Could not create new instance of {}", clazz);
			
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns the id of an entity.
	 * 
	 * @param t the entity
	 * @return the id of the passed entity
	 */
	public static <T> Object getIdValue(T t) {
		String idfield = getIdName(t.getClass());
		return BeanUtils.getPropertyValue(t, idfield);
	}
	
	/**
	 * Finds the class's Id property.
	 * 
	 * @param entityClass
	 *            the entityClass to find the id of
	 * @return the id's property name or null if none is defined
	 */
	public static String getIdName(Class<?> clazz) {
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				for (Annotation annotation : field.getAnnotations()) {
					if (Id.class.isAssignableFrom(annotation.annotationType())) {
						return field.getName();
					}
				}
			}
			clazz = clazz.getSuperclass();
		}

		return null;
	}
	
	/**
	 * Finds the property's "mappedBy" value.
	 * 
	 * @param entityClass
	 *            the entityClass containing the property
	 * @param propertyName
	 *            the name of the property to find the "mappedBy" value for.
	 * @return the value of mappedBy in an annotation.
	 */
	public static String getMappedByProperty(Class<?> entityClass, Object propertyName) {
		OneToMany otm = getAnnotationForProperty(OneToMany.class, entityClass, propertyName.toString());
		if (otm != null && !"".equals(otm.mappedBy())) {
			return otm.mappedBy();
		}
		// Fall back on convention
		return entityClass.getSimpleName().toLowerCase();
	}

	/**
	 * Finds a given annotation on a property.
	 * 
	 * @param annotationType
	 *            the annotation to find.
	 * @param entityClass
	 *            the class declaring the property
	 * @param propertyName
	 *            the name of the property for which to find the annotation.
	 * @return the annotation
	 */
	public static <A extends Annotation> A getAnnotationForProperty(
			Class<A> annotationType, Class<?> entityClass, String propertyName) {
		
		A annotation = getAnnotationFromField(annotationType, entityClass, propertyName);
		if (annotation == null) {
			annotation = getAnnotationFromPropertyGetter(annotationType, entityClass, propertyName);
		}
		return annotation;
	}

	/**
	 * Finds a given annotation on a field.
	 */
	public static <A extends Annotation> A getAnnotationFromField(
			Class<A> annotationType, Class<?> entityClass, String propertyName) {
		
		Field field = null;
		try {
			// TODO: get fields from @mappedsuperclasses as well.
			field = entityClass.getDeclaredField(propertyName);
		} catch (Exception e) {
			// Field not found
		}

		if (field != null && field.isAnnotationPresent(annotationType)) {
			return field.getAnnotation(annotationType);
		}

		return null;
	}

	/**
	 * Finds a given annotation on a property getter method.
	 */
	public static <A extends Annotation> A getAnnotationFromPropertyGetter(
			Class<A> annotationType, Class<?> entityClass, String propertyName) {
		// TODO: support for private getters? -> need to recursively search super classes as well.

		Method getter = null;
		try {
			getter = entityClass.getMethod("get"
					+ propertyName.substring(0, 1).toUpperCase()
					+ propertyName.substring(1));
		} catch (Exception e) {
			// Try isXXX
			try {
				getter = entityClass.getMethod("is"
						+ propertyName.substring(0, 1).toUpperCase()
						+ propertyName.substring(1));
			} catch (Exception e1) {
				// No getter found.
			}
		}
		if (getter != null && getter.isAnnotationPresent(annotationType)) {
			return getter.getAnnotation(annotationType);
		}
		return null;
	}

}
