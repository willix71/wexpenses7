package w.wexpense.vaadin7.support;

import java.io.Serializable;

public class OneToManyRelationship<P, C> implements Serializable {

	private static final long serialVersionUID = -6938432170176885619L;

	private Class<P> parentClass;
	private Class<C> childClass;
	private String parentPropertyId;
	private String childPropertyId;

	public OneToManyRelationship(Class<P> parentClass, Class<C> childClass, String parentPropertyId, String childPropertyId) {
		this.parentClass = parentClass;
		this.childClass = childClass;
		this.parentPropertyId = parentPropertyId;
		this.childPropertyId = childPropertyId;
	}

	public Class<P> getParentClass() {
		return parentClass;
	}

	public Class<C> getChildClass() {
		return childClass;
	}

	public String getParentPropertyId() {
		return parentPropertyId;
	}

	public String getChildPropertyId() {
		return childPropertyId;
	}

}
