package w.wexpense.jsf.controller;

import java.util.List;

import javax.faces.bean.RequestScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import w.wexpense.model.Account;
import w.wexpense.service.model.IAccountService;

//@ManagedBean(name = "accountController")
@Component("jsfAccountController")
@RequestScoped
//@Scope("request")
public class AccountController {

	@Autowired
	private IAccountService accountService;
	
	public long getAccountCount() {
		return accountService.count();
	}
	
	public List<Account> getAllAccounts() {
		return accountService.loadAll();
	}
}
