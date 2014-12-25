package w.wexpense.model;

import java.util.List;

public interface Parentable<T> {

	T getParent();
	
	void setParent(T t);
	
	List<T> getChildren();
}
