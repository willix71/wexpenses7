package w.wexpense.vaadin7;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import w.wexpense.model.Expense;
import w.wexpense.vaadin7.view.GenericView;
import w.wexpense.vaadin7.view.model.ExpenseEditorView;

import com.vaadin.server.Sizeable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.ResizeEvent;

public class WindowSizer implements Serializable {
    private static final long serialVersionUID = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowSizer.class);

    private class WindowSize implements Serializable {
        private static final long serialVersionUID = 1;

        float width;
        Sizeable.Unit widthUnit;
        float height;
        Sizeable.Unit heightUnit;

        public WindowSize(float width, Unit widthUnit, float height, Unit heightUnit) {
            super();
            this.width = width;
            this.widthUnit = widthUnit;
            this.height = height;
            this.heightUnit = heightUnit;
        }

        @Override
        public String toString() {
            return MessageFormat.format(" [{0,number,0} {1};{2,number,0} {3}]", this.width, this.widthUnit, this.height, this.heightUnit);
        }
    }

    public Window.ResizeListener windowSizeSaver = new Window.ResizeListener() {
        private static final long serialVersionUID = 1;

        @Override
        public void windowResized(ResizeEvent e) {
            saveSize(e.getWindow());
        }
    };

    private Map<String, WindowSize> windowsSizes = new HashMap<String, WindowSize>();

    public WindowSizer() {
        // set defaults
        windowsSizes.put(getWindowIdentifier(ExpenseEditorView.class, Expense.class), new WindowSize(850,Unit.PIXELS,650,Unit.PIXELS));
    }
    
    private String getWindowIdentifier(GenericView<?> view) {
        return getWindowIdentifier(view.getClass(),view.getEntityClass());
    }
    @SuppressWarnings("rawtypes")
    private String getWindowIdentifier(Class viewClass, Class entityClass) {
        return viewClass.getSimpleName() + "." + entityClass.getSimpleName();
    }
    public void setSize(Window window) {
        if (window.getContent() instanceof GenericView) {
            WindowSize size = windowsSizes.get(getWindowIdentifier((GenericView<?>) window.getContent()));
            if (size != null) {
                window.setWidth(size.width, size.widthUnit);
                window.setHeight(size.height, size.heightUnit);
            }
        }
    }

    public void saveSize(Window window) {
        if (window.getContent() instanceof GenericView) {
            WindowSize size = new WindowSize(window.getWidth(), window.getWidthUnits(), window.getHeight(), window.getHeightUnits());
            String name = getWindowIdentifier((GenericView<?>) window.getContent());
            LOGGER.debug("Saving size for " + name  + size.toString());
            windowsSizes.put(name, size);
        }
    }
}
