package w.wexpense.persistence;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IGenericDao<T, K  extends Serializable> extends 
	JpaRepository< T, K >, 
	JpaSpecificationExecutor< T > {} 

