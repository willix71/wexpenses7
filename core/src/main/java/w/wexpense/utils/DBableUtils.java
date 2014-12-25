package w.wexpense.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import w.wexpense.model.DBable;
import w.wexpense.persistence.PersistenceUtils;

public class DBableUtils {

	public static Collection<Object> getIds(List<?> entities) {
		Set<Object> uids = new HashSet<Object>();
		for(Object o : entities) {
			uids.add(PersistenceUtils.getIdValue(o));
		}
		return uids;
	}
	
	public static Collection<String> getUids(List<?> entities) {
		Set<String> uids = new HashSet<String>();
		for(Object o : entities) {
			if (o!=null)
				uids.add(((DBable<?>) o).getUid());
		}
		return uids;
	}

}
