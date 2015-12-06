package w.wexpense.rest.web;

import w.expense.rest.dto.DBableDTO;
import w.wexpense.model.DBable;
import w.wexpense.service.StorableService;

public class DBableController<T extends DBable<T>,D extends DBableDTO> extends AbstractController<T, D, Long>{

	public DBableController(StorableService<T, Long> service, Class<T> clazz) throws RuntimeException {
		super(service, clazz);
	}

	@Override
	protected Long getIdFromEntity(T entity) {
		return entity.getId();
	}

	@Override
	protected Long getIdFromDTO(D dto) {
		return dto.getId();
	}
}