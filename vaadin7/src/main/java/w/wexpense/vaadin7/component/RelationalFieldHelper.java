package w.wexpense.vaadin7.component;

import w.wexpense.model.Account;
import w.wexpense.model.Discriminator;
import w.wexpense.model.PayeeType;
import w.wexpense.model.Selectable;

import com.vaadin.data.util.filter.Compare;

public class RelationalFieldHelper {

	public static final RelationalFieldFilter selectableFilter = new RelationalFieldFilter(Selectable.class, new Compare.Equal("selectable", true));
	
	public static final RelationalFieldSorter accountSorter = new RelationalFieldSorter(Account.class, "fullNumber", true); 

	public static final RelationalFieldSorter payeeTypeSorter = new RelationalFieldSorter(PayeeType.class, "name", true);
	
	public static final RelationalFieldCustomizer[] defaultCustomisers = new RelationalFieldCustomizer[]{selectableFilter, accountSorter, payeeTypeSorter};

	public static final RelationalFieldCustomizer[] accountCustomisers = new RelationalFieldCustomizer[]{selectableFilter, accountSorter, cancelFilter(Account.class)};

	public static final RelationalFieldCustomizer[] discriminatorCustomisers = new RelationalFieldCustomizer[]{selectableFilter, accountSorter, cancelFilter(Discriminator.class)};
	
	public static RelationalFieldFilter cancelFilter(Class<?> clazz) {
		return new RelationalFieldFilter(clazz, null);
	}
	
	public static RelationalFieldFilter uidFilter(Class<?> clazz, String uid) {
		return new RelationalFieldFilter(clazz, new Compare.Equal("uid", uid), RelationalFieldFilter.Associater.OR);
	}
}
