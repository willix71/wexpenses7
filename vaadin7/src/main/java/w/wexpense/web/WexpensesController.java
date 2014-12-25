package w.wexpense.web;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WexpensesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WexpensesController.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@RequestMapping("/wexpenses")
	public void helloWord(Model model) {
		
		LOGGER.info("WexpensesController called");
		
		String message = "Hello Wexpenses";
		model.addAttribute("message", message);
	}
	
//	@RequestMapping(value="/dta/{filename}", produces="text/plain")
//	@ResponseBody
//	public String getDtaFileContent(
//			@PathVariable String filename, 
//			@RequestParam(value="singleLine", defaultValue="true") boolean singleLine) throws Exception {
//		
//		LOGGER.info("Fetching {}'s content", filename);
//		
//		Payment payment = paymentService.getPaymentByFilename(filename + ".dta");
//		
//		return getContent(payment, singleLine);
//	}
	
}
