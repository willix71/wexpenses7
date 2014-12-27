package w.wexpense.web;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import w.wexpense.model.Payment;
import w.wexpense.model.PaymentDta;
import w.wexpense.service.model.impl.PaymentService;

@Controller
public class PaymentDtaController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDtaController.class);
	
	@Autowired
	private PaymentService paymentService;
	
	@RequestMapping(value="/dta", produces="text/plain")
	public ResponseEntity<String> getDtaFileContent2(
			@RequestParam(value="id", required=false) Long id,
			@RequestParam(value="uid", required=false) String uid,
			@RequestParam(value="singleLine", defaultValue="true") boolean singleLine,
			@RequestParam(value="inline", defaultValue="true") boolean inline, 
			HttpServletResponse response) throws Exception {

		Payment payment;
		if (id != null) {
			LOGGER.info("Fetching payment with id {}", id);
			payment = paymentService.load(id);
		} else {
			LOGGER.info("Fetching payment with uid {}", uid);
			payment = paymentService.getPaymentByUid(uid);
		}

		if (payment == null) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} else {

			String content = getContent(payment, singleLine);
			HttpHeaders responseHeaders = new HttpHeaders();
			
			if (inline) {    			
    			responseHeaders.add("Content-Type", "text/plain; charset=utf-8");
    			responseHeaders.add("Content-Disposition", "inline;filename=\"" + payment.getFilename() + "\"");
    		}
			
			return new ResponseEntity<String>(content, responseHeaders, HttpStatus.OK);
		}
	}
	
	protected String getContent(Payment payment, boolean singleLine) {
		StringBuilder sb = new StringBuilder();
		for(PaymentDta dta: payment.getDtaLines()) {
			sb.append(dta.getData());
			if (!singleLine) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
