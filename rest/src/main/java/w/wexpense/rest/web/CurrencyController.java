package w.wexpense.rest.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.wexpense.model.Currency;
import w.wexpense.service.StorableService;

@Controller
@RequestMapping(value = "/currency")
public class CurrencyController extends CodableController<Currency> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyController.class);

    @Autowired
    public CurrencyController(StorableService<Currency, String> service) {
    	super(service, Currency.class);
    	
    	LOGGER.info("CountryController initialized");
    }
}
