package w.wexpense.rest.dto;

import java.io.Serializable;

import w.wexpense.rest.exception.ConflictException;

public abstract class AbstractDTO<I extends Serializable> implements Serializable {
	private static final long serialVersionUID = 1L;

	public void checkIdentifier(I identifier) {
		if (identifer()==null) {
			identifer(identifier); // force the id if not set
		} else if (!identifer().equals(identifier)) {
			throw new ConflictException("Ids", identifier, identifer());
		}
	}

	public abstract I identifer();
	protected abstract void identifer(I i);

}
