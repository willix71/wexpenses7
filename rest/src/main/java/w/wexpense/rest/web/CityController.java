package w.wexpense.rest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.wexpense.model.City;
import w.wexpense.rest.dto.CityDTO;
import w.wexpense.service.StorableService;

@Controller
@RequestMapping(value = "/city")
public class CityController extends DBableController<City, CityDTO>{ 

    @Autowired
    public CityController(StorableService<City, Long> service) {
    	super(service, City.class);
    }
}
