package w.wexpense.vaadin7.saver;

import w.wexpense.vaadin7.view.EditorView;

public class RefreshView<T> extends SavingStep<T> {
    
    public RefreshView(EditorView<T, ?> view) {
        super(view, null);
    }
    
    @Override
    public void save(T t) {
        getEditorView().setInstance(t);       
    }
}
