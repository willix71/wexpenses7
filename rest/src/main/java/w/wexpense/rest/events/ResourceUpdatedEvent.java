package w.wexpense.rest.events;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

import w.wexpense.rest.web.AbstractController;

public class ResourceUpdatedEvent extends ApplicationEvent {
	private static final long serialVersionUID = -1680494731266864169L;

	private final HttpServletResponse response;
	private final Class<?> classOfResource;
	private final Object idOfResource;

	public ResourceUpdatedEvent(final AbstractController<?, ?, ?> source, final HttpServletResponse response, final Object idOfResource) {
		super(source);

		this.response = response;
		this.classOfResource = source.getClazz();
		this.idOfResource = idOfResource;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Class<?> getClassOfResource() {
		return classOfResource;
	}

	public Object getIdOfResource() {
		return idOfResource;
	}

}
