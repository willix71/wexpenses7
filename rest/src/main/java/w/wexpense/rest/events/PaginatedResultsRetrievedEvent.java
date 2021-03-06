package w.wexpense.rest.events;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

/**
 * Event that is fired when a paginated search is performed.
 */
public final class PaginatedResultsRetrievedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 7421101291749108216L;
    
	private final HttpServletResponse response;
    
    private final int pageSize;
    private final int page;
    private final int totalPages;
    private final String orderBy; 

    public PaginatedResultsRetrievedEvent(final Object source, final HttpServletResponse responseToSet, final int pageToSet, final int pageSizeToSet, final String orderByField, final int totalPagesToSet) {
        super(source);

        response = responseToSet;
        pageSize = pageSizeToSet;
        page = pageToSet;
        totalPages = totalPagesToSet;
        orderBy = orderByField;
    }

    public final HttpServletResponse getResponse() {
        return response;
    }

    public final int getPageSize() {
        return pageSize;
    }

    public final int getPage() {
        return page;
    }

    public final int getTotalPages() {
        return totalPages;
    }

	public String getOrderBy() {
		return orderBy;
	}
    
    
}
