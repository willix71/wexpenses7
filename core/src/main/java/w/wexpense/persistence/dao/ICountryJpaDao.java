package w.wexpense.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Country;

public interface ICountryJpaDao extends JpaRepository< Country, String >, JpaSpecificationExecutor< Country > {

}

