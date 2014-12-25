package w.wexpense.vaadin7.action;

import w.wexpense.vaadin7.view.SelectorView;

public class SelectHandler extends ListViewAction {

	private SelectorView<?> selectorView;
	
	public SelectHandler(SelectorView<?> selectorView) {
	   super("select");
	   this.selectorView = selectorView;
   }

	@Override
	public void handleAction(Object sender, Object target) {
		selectorView.select();
	}

	@Override
	public boolean canHandle(Object target, Object sender) {
		return true;
	}

}
