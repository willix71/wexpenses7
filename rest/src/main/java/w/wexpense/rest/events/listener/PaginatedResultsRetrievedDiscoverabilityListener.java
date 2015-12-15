package w.wexpense.rest.events.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;

import w.wexpense.rest.events.PaginatedResultsRetrievedEvent;
import w.wexpense.rest.utils.LinkUtil;

@Component
public class PaginatedResultsRetrievedDiscoverabilityListener implements ApplicationListener<PaginatedResultsRetrievedEvent> {

    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String ORDER_BY = "orderBy";
    
    public PaginatedResultsRetrievedDiscoverabilityListener() {
        super();
     }

    @Override
    public final void onApplicationEvent(final PaginatedResultsRetrievedEvent ev) {
        Preconditions.checkNotNull(ev);

        StringBuilder linkHeader = getLinkHeaderValue(ServletUriComponentsBuilder.fromCurrentRequestUri(), ev.getPageSize(), ev.getPage(), ev.getTotalPages()-1, ev.getOrderBy());
        if (linkHeader.length() > 0) {
        	ev.getResponse().addHeader(HttpHeaders.LINK, linkHeader.toString());
        }
    }

    StringBuilder getLinkHeaderValue(final UriComponentsBuilder uriBuilder, final int pageSize, final int page, final int lastPage, final String orderBy) {
        final StringBuilder linkHeader = new StringBuilder();
        
        if (hasNextPage(page, lastPage)) {
        	linkHeader.append(LinkUtil.createLinkHeader(constructPageUri(uriBuilder, page+1, pageSize, orderBy), LinkUtil.REL_NEXT));
        	linkHeader.append(", ");
            linkHeader.append(LinkUtil.createLinkHeader(constructPageUri(uriBuilder, lastPage, pageSize, orderBy), LinkUtil.REL_LAST));
        }
        if (hasPreviousPage(page)) {
        	if (linkHeader.length() > 0) linkHeader.append(", ");
        	
            linkHeader.append(LinkUtil.createLinkHeader(constructPageUri(uriBuilder, page-1, pageSize, orderBy), LinkUtil.REL_PREV));
            linkHeader.append(", ");
            linkHeader.append(LinkUtil.createLinkHeader(constructPageUri(uriBuilder, 0, pageSize, orderBy), LinkUtil.REL_FIRST));
        }
        
        return linkHeader;
    }

    final String constructPageUri(UriComponentsBuilder uriBuilder, final int page, final int size, final String orderBy) {
    	uriBuilder = uriBuilder.replaceQueryParam(PAGE, page).replaceQueryParam(SIZE, size);
    	if (orderBy != null) {
    		uriBuilder.replaceQueryParam(ORDER_BY, orderBy);
    	}
    	return uriBuilder.build().encode().toUriString();
    }
    
    final boolean hasNextPage(final int page, final int lastPage) {
        return page < lastPage; 
    }

    final boolean hasPreviousPage(final int page) {
        return page > 0;
    }
}
