package w.wexpense.rest.events.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import w.wexpense.rest.etag.SimpleVersionManager;
import w.wexpense.rest.events.ResourceUpdatedEvent;

@Component
public class ResourseUpdatedEventListener implements ApplicationListener<ResourceUpdatedEvent> {

	@Autowired
	protected SimpleVersionManager versionManager;
	
    @Override
    public void onApplicationEvent(final ResourceUpdatedEvent event) {
        Preconditions.checkNotNull(event);

        versionManager.setLastVersion(event.getClassOfResource());        
    }
}