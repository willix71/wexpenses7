package w.wexpense.vaadin7.menu;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.vaadin.server.Resource;
import com.vaadin.ui.MenuBar;

public class EnabalebalMenuBar<T> extends MenuBar {

	public interface Enabalebal<T> {
		boolean isEnabled(T t);
	}
	
	private Multimap<Enabalebal<T>, MenuBar.MenuItem> enabalebals = HashMultimap.create();

   public MenuItem addItem(MenuBar.MenuItem parent, String caption, Enabalebal<T> enabalebal, Command command) {
   	MenuItem item = parent.addItem(caption, command);
   	if (enabalebal != null) enabalebals.put(enabalebal, item);
   	return item;
   }

   public MenuItem addItem(MenuBar.MenuItem parent, String caption, Resource icon, Enabalebal<T> enabalebal, Command command) {
   	MenuItem item = parent.addItem(caption, icon, command);
   	if (enabalebal != null) enabalebals.put(enabalebal, item);
   	return item;
   }

   public MenuItem addItemBefore(MenuBar.MenuItem parent, String caption, Resource icon, Enabalebal<T> enabalebal, Command command, MenuItem itemToAddBefore) {
		MenuItem item = parent.addItemBefore(caption, icon, command, itemToAddBefore);
		if (enabalebal != null) enabalebals.put(enabalebal, item);
   	return item;
   }
	
   public void enableMenu(T t) {
   	for(Enabalebal<T> enabable : enabalebals.keySet()) {
   		boolean enabled = enabable.isEnabled(t);
   		for(MenuBar.MenuItem item : enabalebals.get(enabable)) {
   			item.setEnabled(enabled);
   		}
   	}
   }
}
