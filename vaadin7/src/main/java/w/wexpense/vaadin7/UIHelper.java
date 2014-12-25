package w.wexpense.vaadin7;

import java.lang.reflect.Method;

import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.GenericView;

import com.vaadin.data.Property;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.util.ReflectTools;

public class UIHelper {

    public static final Method COMPONENT_EVENT_METHOD = ReflectTools.findMethod(Component.Listener.class, "componentEvent", Component.Event.class);

    public static final WindowSizer windowSizer = new WindowSizer();
    
    public static <T> EditorView<T, ?> getEditorView(Class<T> clazz) {
        String className = clazz.getSimpleName();
        String name = className.substring(0, 1).toLowerCase() + className.substring(1) + "EditorView";
        @SuppressWarnings("unchecked")
        EditorView<T, ?> editor = ((WexUI) UI.getCurrent()).getBean(EditorView.class, name);
        return editor;
    }

    public static Window displayWindow(GenericView<?> editor) {
        Window window = new Window();
        window.setContent(editor);
        editor.setWindow(window);
        windowSizer.setSize(window);
        window.center();
        window.addResizeListener(windowSizer.windowSizeSaver);
        UI.getCurrent().addWindow(window);
        return window;
    }

    public static Window displayModalWindow(GenericView<?> editor) {
        Window window = new Window();
        window.setModal(true);
        window.setContent(editor);
        windowSizer.setSize(window);
        window.center();
        window.addResizeListener(windowSizer.windowSizeSaver);
        editor.setWindow(window);
        UI.getCurrent().addWindow(window);
        return window;
    }

    public static <F> Field<F> setImmediate(Field<F> f) {
        ((AbstractComponent) f).setImmediate(true);
        return f;
    }

    public static <F> Field<F> addValueChangeListener(Field<F> f, Property.ValueChangeListener listener) {
        setImmediate(f).addValueChangeListener(listener);
        return f;
    }

    public static void rightAlign(HorizontalLayout hLayout) {
        Label filler = new Label();
        filler.setSizeFull();
        hLayout.addComponent(filler);
        hLayout.setExpandRatio(filler, 100);
        hLayout.setWidth(100, Unit.PERCENTAGE);
    }
}
