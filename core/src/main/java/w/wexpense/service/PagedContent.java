package w.wexpense.service;

import java.io.Serializable;
import java.util.List;

public class PagedContent<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<T> content;
	private int totalPages;
	
	public PagedContent(List<T> content, int totalPages) {
		super();
		this.content = content;
		this.totalPages = totalPages;
	}

	public List<T> getContent() {
		return content;
	}

	public int getTotalPages() {
		return totalPages;
	}
}