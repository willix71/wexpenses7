package w.wexpense.jsf.controller;

import java.util.List;

import javax.faces.bean.RequestScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import w.wexpense.model.Account;
import w.wexpense.service.model.IAccountService;

@Component("jsfAccountController")
@RequestScoped
public class AccountController {

	@Autowired
	private IAccountService accountService;
	
	private String criteria;
	
	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		System.out.println("Setting criteria " + criteria);
		this.criteria = criteria;
	}

	public long getAccountCount() {
		return accountService.count();
	}
	
	public List<Account> getAllAccounts() {
		System.out.println("Using criteria " + criteria);
		return accountService.easyFind(criteria);
	}
	
	public String filter() {
		return null;
	}
}
