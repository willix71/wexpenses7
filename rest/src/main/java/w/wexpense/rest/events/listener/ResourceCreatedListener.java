package w.wexpense.rest.events.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.base.Preconditions;

import w.wexpense.rest.etag.SimpleVersionManager;
import w.wexpense.rest.events.ResourceCreatedEvent;

@Component
public class ResourceCreatedListener implements ApplicationListener<ResourceCreatedEvent> {

	@Autowired
	protected SimpleVersionManager versionManager;
	
    @Override
    public void onApplicationEvent(final ResourceCreatedEvent event) {
        Preconditions.checkNotNull(event);

        addLinkHeaderOnResourceCreation(event.getResponse(), event.getIdOfNewResource());
        
        versionManager.setLastVersion(event.getClassOfNewResource());
    }

    void addLinkHeaderOnResourceCreation(final HttpServletResponse response, final Object idOfNewResource) {
        // final String requestUrl = request.getRequestURL().toString();
        // final URI uri = new UriTemplate("{requestUrl}/{idOfNewResource}").expand(requestUrl, idOfNewResource);

        final URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{idOfNewResource}").buildAndExpand(idOfNewResource).toUri();
        
        response.setHeader(HttpHeaders.LOCATION, uri.toASCIIString());
    }

}