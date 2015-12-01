package w.wexpense.service.model;

import org.springframework.transaction.annotation.Transactional;

import w.wexpense.model.Repeater;
import w.wexpense.service.StorableService;

public interface IRepeaterService extends StorableService<Repeater, Long> {

	@Transactional
	void execute();

}
