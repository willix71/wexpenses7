package w.wexpense.model;

import javax.persistence.Entity;

@Entity
public class PayeeType extends AbstractType<PayeeType> {

	private static final long serialVersionUID = 2482940442245899869L;

	public PayeeType() {
	   super();
   }

	public PayeeType(String name) {
	   super(name);
   }
	
	public PayeeType(String name, boolean selectable) {
	   super(name, selectable);
   }

}
