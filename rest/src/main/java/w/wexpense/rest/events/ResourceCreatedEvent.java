package w.wexpense.rest.events;


import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

public class ResourceCreatedEvent extends ApplicationEvent {
	private static final long serialVersionUID = -1680494731266864169L;

	private final HttpServletResponse response;
    private final Object idOfNewResource;

    public ResourceCreatedEvent(final Object source, final HttpServletResponse response, final Object idOfNewResource) {
        super(source);

        this.response = response;
        this.idOfNewResource = idOfNewResource;
    }

    // API

    public HttpServletResponse getResponse() {
        return response;
    }

    public Object getIdOfNewResource() {
        return idOfNewResource;
    }

}
