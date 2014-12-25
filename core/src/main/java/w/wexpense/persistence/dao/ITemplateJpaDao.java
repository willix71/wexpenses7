package w.wexpense.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import w.wexpense.model.Template;

public interface ITemplateJpaDao extends JpaRepository< Template, Long >, JpaSpecificationExecutor< Template >, IDBableJpaDao<Template> {

	List<Template> findByTemplateName(String name);
	
	List<Template> findByTemplateMenu(String name);
	
	//List<Template> findByTemplateOrderNotNllOrderByTemplateMenuOrderByTemplateOrder();
}
