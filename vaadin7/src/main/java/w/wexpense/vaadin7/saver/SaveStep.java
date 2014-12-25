package w.wexpense.vaadin7.saver;

import w.wexpense.vaadin7.view.EditorView;


public class SaveStep<T> extends SavingStep<T> {    
    
    public SaveStep(EditorView<T, ?> view, SavingStep<T> nextStep) {
        super(view, nextStep);
    }
    
    @Override
    public void save(T t) {
        T t2 = getEditorView().getStoreService().save(t);
        
        getEditorView().fireEntityChange(t2);
        
        nextStep(t2);
    } 
}
