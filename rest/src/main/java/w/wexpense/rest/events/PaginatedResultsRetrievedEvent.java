package w.wexpense.rest.events;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Event that is fired when a paginated search is performed.
 */
public final class PaginatedResultsRetrievedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 7421101291749108216L;

	private final UriComponentsBuilder uriBuilder;
    
	private final HttpServletResponse response;
    
    private final int pageSize;
    private final int page;
    private final int totalPages;
    private final String orderBy; 


    public PaginatedResultsRetrievedEvent(final Object source, final UriComponentsBuilder uriBuilderToSet, final HttpServletResponse responseToSet, final int pageSizeToSet, final int pageToSet, final int totalPagesToSet, final String orderByField) {
        super(source);

        uriBuilder = uriBuilderToSet;
        response = responseToSet;
        pageSize = pageSizeToSet;
        page = pageToSet;
        totalPages = totalPagesToSet;
        orderBy = orderByField;
    }

    // API

    public final UriComponentsBuilder getUriBuilder() {
        return uriBuilder;
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
