package w.wexpense.jsf.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;

@Configuration
public class ServiceLocator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLocator.class);

	private static ApplicationContext context;

	@EventListener({ ApplicationContextEvent.class })
	public void onApplicationEvent(ApplicationContextEvent event) {
		LOGGER.info("Loading context " + event.getApplicationContext());

		// do the following for the main application context only
		// specific servlet context should not overload the static variables
		if (event.getApplicationContext().getParent() == null) {
			if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
				context = event.getApplicationContext();
				LOGGER.info("Setting application context");
			} else if (event instanceof ContextStoppedEvent || event instanceof ContextClosedEvent) {
				LOGGER.info("Clearing application context");
				context = null;
			}
		}
	}

	public static <T> T getService(Class<T> clazz) {
		return context.getBean(clazz);
	}

	public static <T> T getService(String name, Class<T> clazz) {
		return context.getBean(name, clazz);
	}
}
