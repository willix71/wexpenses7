package w.wexpense.rest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.wexpense.model.ExchangeRate;
import w.wexpense.rest.dto.ExchangeRateDTO;
import w.wexpense.service.StorableService;

@Controller
@RequestMapping(value = "/exchangeRate")
public class ExchangeRateController extends DBableController<ExchangeRate, ExchangeRateDTO>{ 

    @Autowired
    public ExchangeRateController(StorableService<ExchangeRate, Long> service) {
    	super(service, ExchangeRate.class);
    }
}
