package w.wexpense.rest.events;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

import w.wexpense.rest.web.AbstractController;

public class ResourceCreatedEvent extends ApplicationEvent {
	private static final long serialVersionUID = -1680494731266864169L;

	private final HttpServletResponse response;
	private final Class<?> classOfNewResource;
    private final Object idOfNewResource;

    public ResourceCreatedEvent(final AbstractController<?,?,?> source, final HttpServletResponse response, final Object idOfNewResource) {
        super(source);

        this.response = response;
        this.classOfNewResource = source.getClazz();
        this.idOfNewResource = idOfNewResource;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Class<?> getClassOfNewResource() {
		return classOfNewResource;
	}

	public Object getIdOfNewResource() {
        return idOfNewResource;
    }
}
