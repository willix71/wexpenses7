package w.wexpense.service;

import java.io.Serializable;
import java.util.List;

public class PagedContent<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<T> content;
	private long totalElements;
	private int totalPages;
	
	public PagedContent(List<T> content, long totalElements, int totalPages) {
		super();
		this.content = content;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

	public List<T> getContent() {
		return content;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}
}