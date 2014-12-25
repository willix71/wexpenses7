package w.wexpense.vaadin7.view;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author willix
 */
@Component
@Scope("prototype")
@VaadinView(MainView.NAME)
public class MainView extends Panel implements View
{
    public static final String NAME = "";

    @PostConstruct
    public void PostConstruct()
    {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);      
        layout.addComponent(new Link("AllInOne", new ExternalResource("#!" + AllInOneView.NAME)));
        setContent(layout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
       setSizeFull();
    }
}
