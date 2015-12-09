package w.wexpense.rest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.wexpense.model.Account;
import w.wexpense.rest.dto.AccountDTO;
import w.wexpense.service.StorableService;

@Controller
@RequestMapping(value = "/account")
public class AccountController extends DBableController<Account, AccountDTO>{ 

    @Autowired
    public AccountController(StorableService<Account, Long> service) {
    	super(service, Account.class);
    }
}
