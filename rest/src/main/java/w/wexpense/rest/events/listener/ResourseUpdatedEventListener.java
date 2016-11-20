package w.wexpense.rest.events.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import w.wexpense.rest.events.ResourceUpdatedEvent;

@Component
public class ResourseUpdatedEventListener implements ApplicationListener<ResourceUpdatedEvent> {
	
    @Override
    public void onApplicationEvent(final ResourceUpdatedEvent event) {
        Preconditions.checkNotNull(event);            
    }
}