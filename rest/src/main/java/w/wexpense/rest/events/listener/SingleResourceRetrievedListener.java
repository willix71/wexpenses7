package w.wexpense.rest.events.listener;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;

import w.wexpense.rest.events.SingleResourceRetrievedEvent;
import w.wexpense.rest.utils.LinkUtil;

@Component
public class SingleResourceRetrievedListener implements ApplicationListener<SingleResourceRetrievedEvent> {

    @Override
    public void onApplicationEvent(final SingleResourceRetrievedEvent resourceRetrievedEvent) {
        Preconditions.checkNotNull(resourceRetrievedEvent);

        addLinkHeaderOnSingleResourceRetrieval(resourceRetrievedEvent.getResponse());
    }

    void addLinkHeaderOnSingleResourceRetrieval(final HttpServletResponse response) {
        final String requestURL = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri().toASCIIString();
        
        final String uriForCollection = requestURL.substring(0, requestURL.lastIndexOf("/"));

        final String linkHeaderValue = LinkUtil.createLinkHeader(uriForCollection, "collection");
        
        response.addHeader(HttpHeaders.LINK, linkHeaderValue);
    }
}