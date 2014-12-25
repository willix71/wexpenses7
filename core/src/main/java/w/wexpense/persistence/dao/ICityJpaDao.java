package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.City;

public interface ICityJpaDao extends JpaRepository< City, Long >, JpaSpecificationExecutor< City >, IDBableJpaDao<City>  {

}

