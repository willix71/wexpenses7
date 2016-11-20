package w.wexpense.rest.dto;

public class CodableDTO extends AbstractDTO<String> {
	private static final long serialVersionUID = 1L;

	private String code;
	private String name;

	public CodableDTO() {}
	
	public CodableDTO(String code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	public String identifer() {
		return getCode();
	}
	@Override
	public void identifer(String code) {
		setCode(code);
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodableDTO other = (CodableDTO) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
