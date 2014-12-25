package w.wexpense.vaadin7.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

public class WindowView extends Panel {

	private static final long serialVersionUID = 5282517667310057582L;
	
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private String viewName;
	
	private Window window;

	public Window getWindow() {
		return window;
	}
	
	public void setWindow(Window window) {
		this.window = window;
	}
	
	public String getViewName() {
		return viewName;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public void close() {
		window.close();
	}
	
	public Button getCloseButton() {
		return new Button("Close", new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
	
			@Override
			public void buttonClick(ClickEvent event) {
				close();			
			}
		});
	}
}
