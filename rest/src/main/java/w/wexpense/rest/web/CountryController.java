package w.wexpense.rest.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.wexpense.model.Country;
import w.wexpense.rest.dto.CountryDTO;
import w.wexpense.service.StorableService;

@Controller
@RequestMapping(value = "/country")
public class CountryController extends CodableController<Country, CountryDTO>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);

    @Autowired
    public CountryController(StorableService<Country, String> service) {
    	super(service, Country.class);
    	
    	LOGGER.info("CountryController initialized");
    }
}
