package w.wexpense.vaadin7.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import w.wexpense.model.DBable;
import w.wexpense.model.Duplicatable;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;
import w.wexpense.vaadin7.view.SelectorView;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.Or;

public class ActionHelper {

	public static <T, ID extends Serializable, E extends EditorView<T, ID>> 
	void setDefaultListViewActions(ListView<T> view, String editorName, ListViewAction ...actions) {
		ActionHandler handler = new ActionHandler();
		handler.addListViewAction(new AddNewAction(editorName));
		handler.addListViewAction(new EditAction(editorName), true);
		handler.addListViewAction(new DeleteAction(editorName));
		if (Duplicatable.class.isAssignableFrom(view.getEntityClass())) {
			handler.addListViewAction(new DuplicateAction(editorName));
		}
		handler.addListViewAction(new RefreshAction());
		
		for(ListViewAction a: actions) {
			handler.addListViewAction(a);
		}
		view.setActionHandler(handler);
	}

	public static <T, ID extends Serializable, E extends EditorView<T, ID>> 
	void setDefaultSelectorViewActions(SelectorView<T> view, String editorName, ListViewAction ...actions) {
		ActionHandler handler = new ActionHandler();
		handler.addListViewAction(new SelectHandler(view), true);
		handler.addListViewAction(new AddNewAction(editorName));
		handler.addListViewAction(new EditAction(editorName));
		handler.addListViewAction(new DeleteAction(editorName));
		handler.addListViewAction(new RefreshAction());
		
		for(ListViewAction a: actions) {
			handler.addListViewAction(a);
		}
		view.setActionHandler(handler);
	}

	public static <T, ID extends Serializable, E extends EditorView<T, ID>> 
	void setExchangeRateSelectorViewActions(SelectorView<T> view, String editorName) {
		ActionHandler handler = new ActionHandler();
		handler.addListViewAction(new SelectHandler(view), true);
		handler.addListViewAction(new AddNewAction(editorName));
		handler.addListViewAction(new DeleteAction(editorName));
		handler.addListViewAction(new RefreshAction());
		view.setActionHandler(handler);
	}
	
	public static Filter parentFilter(String parentPropertyId, DBable parent) {
		Filter filter = new IsNull(parentPropertyId);
		if (!parent.isNew()) {
			filter = new Or(filter, new Compare.Equal(parentPropertyId, parent));
		}
		return filter;
   }
	
	public static <D extends DBable> Filter excludeFilter(Collection<D> dbables) {		
		List<Filter> excluded = new ArrayList<Filter>();
		for(DBable d: dbables) {
			if (!d.isNew()) {
				excluded.add(new Compare.Equal("id", d.getId()));
			}
		}
		if (excluded.isEmpty()) {
			return null;
		} else {
			return new Not(new Or(excluded.toArray(new Filter[excluded.size()])));
		}
	}
}
