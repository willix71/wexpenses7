package w.wexpense.persistence.dao;

import java.util.List;

import w.wexpense.model.Template;
import w.wexpense.persistence.IUidableDao;
import w.wexpense.persistence.IGenericDao;

public interface ITemplateJpaDao extends IGenericDao< Template, Long >, IUidableDao<Template> {

	List<Template> findByTemplateName(String name);
	
	List<Template> findByTemplateMenu(String name);
	
	//List<Template> findByTemplateOrderNotNllOrderByTemplateMenuOrderByTemplateOrder();
}
