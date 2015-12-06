package w.wexpense.rest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.expense.rest.dto.PayeeDTO;
import w.wexpense.model.Payee;
import w.wexpense.service.StorableService;

@Controller
@RequestMapping(value = "/payee")
public class PayeeController extends DBableController<Payee, PayeeDTO>{ 

    @Autowired
    public PayeeController(StorableService<Payee, Long> service) {
    	super(service, Payee.class);
    }
}
