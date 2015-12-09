package w.wexpense.rest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.wexpense.model.Currency;
import w.wexpense.rest.dto.CurrencyDTO;
import w.wexpense.service.StorableService;

@Controller
@RequestMapping(value = "/currency")
public class CurrencyController extends CodableController<Currency, CurrencyDTO> {
	
    @Autowired
    public CurrencyController(StorableService<Currency, String> service) {
    	super(service, Currency.class);
    }
}
