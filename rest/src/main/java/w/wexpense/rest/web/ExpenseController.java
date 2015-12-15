package w.wexpense.rest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.rest.dto.ExpenseDTO;
import w.wexpense.service.StorableService;

@Controller
@RequestMapping(value = "/expense")
public class ExpenseController extends DBableController<Expense, ExpenseDTO> {

	@Autowired
	public ExpenseController(StorableService<Expense, Long> service) {
		super(service, Expense.class);
	}

	@Override
	protected Expense fromDto(ExpenseDTO dto, Expense entity) {
		Expense x = super.fromDto(dto, entity);
		for(TransactionLine line: x.getTransactions()) line.setExpense(x);
		return x;
	}
	
	
}
