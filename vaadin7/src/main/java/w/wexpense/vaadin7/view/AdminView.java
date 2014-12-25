package w.wexpense.vaadin7.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import w.wexpense.persistence.PersistenceConfiguration;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@org.springframework.stereotype.Component
@PropertySource("classpath:maven.properties")
public class AdminView extends WindowView {

    private static final long serialVersionUID = 1L;

    @Value("${wexpenses_env:dev}")
    private String env;
    
    @Autowired
    private PersistenceConfiguration persistenceConfiguration;
	
    @Autowired
    private Environment buildEnvironment;
    
    public AdminView() {
		setViewName("Admin");		
	}

    @PostConstruct
    public void PostConstruct() {
        GridLayout layout = new GridLayout(2,4);
        layout.setSpacing(true);
        layout.setMargin(true);      
        layout.addComponent(new Label("Version"));
        layout.addComponent(new Label(buildEnvironment.getProperty("version")));
        layout.addComponent(new Label("Built"));
        layout.addComponent(new Label(buildEnvironment.getProperty("built.at")));
        layout.addComponent(new Label("Environment"));
        layout.addComponent(new Label(env));
        layout.addComponent(new Label("Database"));
        layout.addComponent(new Label(persistenceConfiguration.getUrl()));
        setContent(layout);
    }
}
