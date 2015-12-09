package w.wexpense.rest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.wexpense.model.PayeeType;
import w.wexpense.rest.dto.PayeeTypeDTO;
import w.wexpense.service.StorableService;

@Controller
@RequestMapping(value = "/payeeType")
public class PayeeTypeController extends DBableController<PayeeType, PayeeTypeDTO> {

	@Autowired
	public PayeeTypeController(StorableService<PayeeType, Long> service) {
		super(service, PayeeType.class);
	}
}
