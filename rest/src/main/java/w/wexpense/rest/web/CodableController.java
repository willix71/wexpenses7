package w.wexpense.rest.web;

import w.wexpense.model.Codable;
import w.wexpense.rest.dto.CodableDTO;
import w.wexpense.service.StorableService;

public class CodableController<T extends Codable<T>,D extends CodableDTO> extends AbstractController<T, D, String>{

	public CodableController(StorableService<T, String> service, Class<T> clazz) {
		super(service, clazz);
    }
}