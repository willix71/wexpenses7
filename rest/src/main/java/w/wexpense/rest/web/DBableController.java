package w.wexpense.rest.web;

import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import w.wexpense.model.DBable;
import w.wexpense.rest.dto.DBableDTO;
import w.wexpense.rest.events.SingleResourceRetrievedEvent;
import w.wexpense.rest.utils.RestPreconditions;
import w.wexpense.service.StorableService;

public class DBableController<T extends DBable<T>,D extends DBableDTO> extends AbstractController<T, D, Long>{

	public DBableController(StorableService<T, Long> service, Class<T> clazz) throws RuntimeException {
		super(service, clazz);
	}
	
	@Override
	protected T dto2Entity(D dto, T entity) {
		if (dto.getUid() == null) {
			dto.setUid(DBable.newUid());
		}
		return super.dto2Entity(dto, entity);
	}

	protected void versionCheck(Long id, String version, HttpServletResponse response) {
		//versionManager.checkAndSet(clazz, id, version, response);
	}
	
	/**
     * curl -i http://localhost:8880/spring/rest/foos?uid=12345
     * 
     */
    @RequestMapping(params = { "uid" }, method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly=true)
    public D findByUid(@RequestParam("uid") final String uid, final HttpServletResponse response) {
        final T resourceByUid = RestPreconditions.checkFound(service.loadByUid(uid));
        
        eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, response));
        
        return entity2Dto(resourceByUid);
    }
}