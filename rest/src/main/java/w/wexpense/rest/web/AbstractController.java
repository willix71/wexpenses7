package w.wexpense.rest.web;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.Preconditions;

import w.wexpense.rest.events.PaginatedResultsRetrievedEvent;
import w.wexpense.rest.events.ResourceCreatedEvent;
import w.wexpense.rest.events.SingleResourceRetrievedEvent;
import w.wexpense.rest.exception.ResourceNotFoundException;
import w.wexpense.rest.utils.LinkUtil;
import w.wexpense.rest.utils.RestPreconditions;
import w.wexpense.service.PagedContent;
import w.wexpense.service.StorableService;

public abstract class AbstractController<T, D, ID extends Serializable> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected ModelMapper modelMapper;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	protected final StorableService<T, ID> service;
	protected final Class<T> clazz;
	protected final Class<D> clazzDTO;

	@SuppressWarnings("unchecked")
	public AbstractController(StorableService<T, ID> service, Class<T> clazz) throws RuntimeException {
		LOGGER.info("Controller initialized");

		this.service = service;
		this.clazz = clazz;

		try {
			this.clazzDTO = (Class<D>) Class.forName("w.wexpense.rest.dto." + clazz.getSimpleName() + "DTO");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract ID getIdFromEntity(T entity);

	protected abstract ID getIdFromDTO(D dto);

	protected T fromDto(D dto) {
		T entity = modelMapper.map(dto, clazz);
		return service.save(entity);
	}

	protected D toDto(T t) {
		D dto = modelMapper.map(t, clazzDTO);
		return dto;
	}

	protected List<D> toDtos(List<T> ts) {
		return ts.stream().map(post -> toDto(post)).collect(Collectors.toList());
	}

	/**
	 * curl -i http://localhost:8880/spring/rest/foos/1
	 * 
	 * curl -i --header “Accept: application/json”
	 * http://localhost:8880/spring/rest/foos/1
	 * 
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public D findById(@PathVariable("id") final ID id, final HttpServletResponse response) {
		final T resourceById = RestPreconditions.checkFound(service.load(id));
		eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, response));

		return toDto(resourceById);
	}

	/**
	 * curl -i http://localhost:8880/spring/rest/foos
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<D> findAll() {
		return toDtos(service.loadAll());
	}

	/**
	 * curl -i http://localhost:8880/spring/rest/foos?page=0&size=5
	 * 
	 */
	@RequestMapping(params = { "page" }, method = RequestMethod.GET)
	@ResponseBody
	public List<D> findPaginated(@RequestParam("page") final int page,
			@RequestParam(value = "size", defaultValue = "10") final int size,
			@RequestParam(value = "orderBy", required = false) final String orderBy,
			final UriComponentsBuilder uriBuilder, final HttpServletResponse response) {

		PagedContent<T> resultPage = service.loadPage(page, size, orderBy);

		if (page >= resultPage.getTotalPages()) {
			throw new ResourceNotFoundException();
		}

		eventPublisher.publishEvent(
				new PaginatedResultsRetrievedEvent(this, LinkUtil.getRequestMapping(uriBuilder, this.getClass()),
						response, size, page, resultPage.getTotalPages(), orderBy));

		return toDtos(resultPage.getContent());
	}

	// write

	/**
	 * curl -i http://localhost:8880/spring/rest/foos?page=0&size=5 curl -iH
	 * "Content-Type: application/json" -X POST -d '{"name":"henri"}'
	 * http://localhost:8880/spring/rest/foos
	 *
	 * or save the json to a file and replace it by @filename
	 * 
	 * or -d param=value&param2=value2
	 * 
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void create(@RequestBody final D dto, final HttpServletResponse response) {
		Preconditions.checkNotNull(dto);
		final ID idOfCreatedResource = getIdFromEntity(fromDto(dto));

		eventPublisher.publishEvent(new ResourceCreatedEvent(this, response, idOfCreatedResource));
	}

	/**
	 * curl -iH "Content-Type: application/json" -X PUT -d
	 * '{"id":"1","name":"willy123"}' http://localhost:8880/spring/rest/foos/1
	 * 
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public void update(@PathVariable("id") final ID id, @RequestBody final D dto) {
		Preconditions.checkNotNull(dto);
		if (!getIdFromDTO(dto).equals(id)) {
			throw new IllegalArgumentException("None matching ids");
		}

		RestPreconditions.checkFound(service.load(getIdFromDTO(dto)));

		fromDto(dto);
	}

	/**
	 * curl -iH "Content-Type: application/json" -X PATCH -d
	 * '{"name":"willy123"}' http://localhost:8880/spring/rest/foos/1
	 * 
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void patch(@PathVariable("id") final ID id, @RequestBody final T resource) {
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
	public void delete(@PathVariable("id") final ID id) {
		service.delete(id);
	}

}
