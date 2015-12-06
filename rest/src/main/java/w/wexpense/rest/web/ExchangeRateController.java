package w.wexpense.rest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.expense.rest.dto.ExchangeRateDTO;
import w.wexpense.model.ExchangeRate;
import w.wexpense.service.StorableService;

@Controller
@RequestMapping(value = "/rate")
public class ExchangeRateController extends DBableController<ExchangeRate, ExchangeRateDTO>{ 

    @Autowired
    public ExchangeRateController(StorableService<ExchangeRate, Long> service) {
    	super(service, ExchangeRate.class);
    }
}
