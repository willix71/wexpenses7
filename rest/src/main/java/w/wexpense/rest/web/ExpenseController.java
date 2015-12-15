package w.wexpense.rest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import w.wexpense.model.Expense;
import w.wexpense.model.TransactionLine;
import w.wexpense.rest.dto.ExpenseDTO;
import w.wexpense.service.model.IExpenseService;

@Controller
@RequestMapping(value = "/expense")
public class ExpenseController extends DBableController<Expense, ExpenseDTO> {

	@Autowired
	public ExpenseController(IExpenseService service) {
		super(service, Expense.class);
	}

	@Override
	protected Expense dto2Entity(ExpenseDTO dto, Expense entity) {
		Expense x = super.dto2Entity(dto, entity);
		for(TransactionLine tl: x.getTransactions()) {
			tl.setExpense(x);
		}
		return x;
	}
}
