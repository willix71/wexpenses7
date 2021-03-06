package w.wexpense.rest.dto;

public class DBableDTO extends AbstractDTO<Long> {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long version;
	private String uid;
	private String display;
	
	public DBableDTO() {}

	public DBableDTO(Long id, Long version, String uid) {
		this.id = id;
		this.version = version;
		this.uid = uid;
	}
	public DBableDTO(Long id, Long version, String uid, String display) {
		this(id,version,uid);
		this.display = display;
	}

	@Override
	public Long identifer() {
		return getId();
	}

	@Override
	public void identifer(Long i) {
		setId(i);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	@Override	
	public String toString() {
		return display;
	}
}
