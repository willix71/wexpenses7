package w.wexpense.vaadin7.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;

public class ActionHandler implements Action.Handler, ItemClickListener {

	private static final long serialVersionUID = 1L;

	protected Table table;

	protected ListViewAction defaultAction;
	
	protected List<ListViewAction> actions = new ArrayList<>();
	
	protected boolean enabled = true;

   public ActionHandler() {}
   
   public ActionHandler(ListViewAction ...actions) {
       this.actions.addAll(Arrays.asList(actions));
   }
   
	public void setTable(Table table) {
		this.table = table;

		table.addActionHandler(this);
		table.addItemClickListener(this);
	}

	@Override
	public Action[] getActions(final Object target, final Object sender) {	
		Collection<ListViewAction> c = Collections2.filter(actions,
				new Predicate<ListViewAction>() {
					public boolean apply(@Nullable ListViewAction action) {
						return enabled && action.canHandle(target, sender);
					}
				});
		return c.toArray(new Action[c.size()]);
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {
		((Action.Listener) action).handleAction(sender, target);
	}

	public void addListViewAction(ListViewAction action, boolean defaultAction) {
		addListViewAction(action);
		if (defaultAction)
			setDefaultAction(action);
	}

	public void addListViewAction(ListViewAction action) {
		actions.add(action);
	}

	public void setListViewActions(List<ListViewAction> actions) {
		this.actions = actions;
	}

	public void setDefaultAction(ListViewAction defaultAction) {
		this.defaultAction = defaultAction;
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		if (event.isDoubleClick()) {
			// keep the selection active because double clicks = 2 clicks, the
			// first selects, the second unselects !!!
			table.select(event.getItemId());

			entitySelected(event.getItemId());
		}
	}

	public void entitySelected(Object target) {
		if (defaultAction != null
				&& defaultAction.canHandle(target, this.table)) {
			defaultAction.handleAction(table, target);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		requestRepaint();
	}
	
   private void requestRepaint() {
      if (table != null) {
          table.markAsDirtyRecursive();
      }
  }
}
