package w.wexpense.vaadin7.saver;

import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.ui.Window;

public class CloseWindow<T> extends SavingStep<T> {

    public CloseWindow(EditorView<T, ?> view) {
        super(view, null);
    }
    
    @Override
    public void save(T t) {
        Window w = getEditorView().getWindow();
        if (w!=null) w.close();       
    }

}
