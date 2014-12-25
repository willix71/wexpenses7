package w.wexpense.vaadin7.action;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Table;

import w.wexpense.service.model.ITransactionLineService;

/**
 * An action to trigger a full balance on all transaction line
 * @author willy
 *
 */
public class BalanceTransactionLineAction extends ListViewAction {

	private static final long serialVersionUID = 1L;

	private ITransactionLineService service;
	
	public BalanceTransactionLineAction(ITransactionLineService service) {
		super("balance");
		this.service = service;
	}	

	@Override
	public void handleAction(final Object sender, final Object target) {
		try {
			service.balanceTransactionLine();
			com.vaadin.data.Container c = ((Table) sender) .getContainerDataSource();
			((JPAContainer<?>) c).refresh();
			
		} catch(Exception e) {
			LOGGER.error("failed to balance transaction lines", e);
		}
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return true;
	}

}
