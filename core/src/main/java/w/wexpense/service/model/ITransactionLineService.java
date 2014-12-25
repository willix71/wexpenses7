package w.wexpense.service.model;

import org.springframework.transaction.annotation.Transactional;

import w.wexpense.model.TransactionLine;
import w.wexpense.service.StorableService;

public interface ITransactionLineService extends StorableService<TransactionLine, Long> {

	@Transactional
	void balanceTransactionLine() throws Exception;
}
