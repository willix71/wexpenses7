package w.wexpense.rest.web;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.Preconditions;

import w.expense.rest.events.PaginatedResultsRetrievedEvent;
import w.expense.rest.events.ResourceCreatedEvent;
import w.expense.rest.events.SingleResourceRetrievedEvent;
import w.wexpense.model.Codable;
import w.wexpense.rest.exception.MyResourceNotFoundException;
import w.wexpense.rest.utils.LinkUtil;
import w.wexpense.rest.utils.RestPreconditions;
import w.wexpense.service.StorableService;

public class CodableController<T extends Codable<T>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CodableController.class);
	
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final StorableService<T, String> service;

    private final Class<T> clazz;
    
    public CodableController(StorableService<T, String> service, Class<T> clazz) {
    	LOGGER.info("DbableController initialized");
    	
    	this.service = service;
    	this.clazz=clazz;
    }

    /**
     * curl -i http://localhost:8880/spring/rest/foos/1
     * 
     * curl -i --header “Accept: application/json” http://localhost:8880/spring/rest/foos/1
     * 
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public T findById(@PathVariable("id") final String id, final HttpServletResponse response) {
        final T resourceById = RestPreconditions.checkFound(service.load(id));

        eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, response));
        
        return resourceById;
    }

    /**
     * curl -i http://localhost:8880/spring/rest/foos
     * 
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<T> findAll() {
        return service.loadAll();
    }

    /**
     * curl -i http://localhost:8880/spring/rest/foos?page=0&size=5
     * 
     */
    @RequestMapping(params = { "page"}, method = RequestMethod.GET)
    @ResponseBody
    public List<T> findPaginated(@RequestParam("page") final int page, @RequestParam(value="size", defaultValue="10") final int size, final UriComponentsBuilder uriBuilder, final HttpServletResponse response) {
    	List<T> all = service.loadAll();
		int fromIndex = page*size;
		if (fromIndex >= all.size()) {
			throw new MyResourceNotFoundException();			
		}
		int toIndex = fromIndex + size;
		if (toIndex > all.size()) toIndex = all.size();
		
		int totalPages = all.size() / size;
		if (all.size() % size > 0) totalPages++;
		
        eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent(this, LinkUtil.plural(uriBuilder, this.clazz), response, size, page, totalPages));

        return all.subList(fromIndex, toIndex);
    }

    // write

    /**curl -i http://localhost:8880/spring/rest/foos?page=0&size=5
     * curl -iH "Content-Type: application/json" -X POST -d '{"name":"henri"}' http://localhost:8880/spring/rest/foos
     *
     * or save the json to a file and replace it by @filename
     * 
     * or -d param=value&param2=value2
     * 
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final T resource, final HttpServletResponse response) {
        Preconditions.checkNotNull(resource);
        final String idOfCreatedResource = service.save(resource).getCode();

        eventPublisher.publishEvent(new ResourceCreatedEvent(this, response, idOfCreatedResource));
    }

    /**
     * curl -iH "Content-Type: application/json" -X PUT -d '{"id":"1","name":"willy123"}' http://localhost:8880/spring/rest/foos/1
     * 
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("id") final String id, @RequestBody final T resource) {
        Preconditions.checkNotNull(resource);
        if (!resource.getCode().equals(id)) {
        	throw new IllegalArgumentException("None matching ids");
        }
        
        RestPreconditions.checkFound(service.load(resource.getCode()));
        service.save(resource);
    }

    /**
     * curl -iH "Content-Type: application/json" -X PATCH -d '{"name":"willy123"}' http://localhost:8880/spring/rest/foos/1
     * 
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patch(@PathVariable("id") final Long id, @RequestBody final T resource) {
        Preconditions.checkNotNull(resource);

        // TODO
        
        LOGGER.info("Patching " + resource);
    }

    
    /**
     * curl -i -X DELETE http://localhost:8880/spring/rest/foos/1
     * 
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") final String id) {
        service.delete(id);
    }

}
