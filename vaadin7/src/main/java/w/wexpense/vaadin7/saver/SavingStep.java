package w.wexpense.vaadin7.saver;

import w.wexpense.vaadin7.view.EditorView;

public abstract class SavingStep<T> {
    private EditorView<T, ?> editorView;
    
    private SavingStep<T> nextStep;
        
    public SavingStep(EditorView<T, ?> editorView, SavingStep<T> nextStep) {
        super();
        this.editorView = editorView;
        this.nextStep = nextStep;
    }

    public void setEditorView(EditorView<T, ?> editorView) {
        this.editorView = editorView;
    }

    public EditorView<T, ?> getEditorView() {
        return editorView;
    }

    public void setNextStep(SavingStep<T> nextStep) {
        this.nextStep = nextStep;
    }
    
    public void nextStep(T t) {
        if (nextStep != null) {
            nextStep.save(t);
        }
    }
    
    public abstract void save(T t);
    

}
