package w.expense.rest.dto;

public class PayeeTypeDTO extends DBableDTO {
	private static final long serialVersionUID = 1L;

	private String name;
	private boolean selectable;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSelectable() {
		return selectable;
	}
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
}
