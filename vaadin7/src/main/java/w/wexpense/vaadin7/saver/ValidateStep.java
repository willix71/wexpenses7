package w.wexpense.vaadin7.saver;

import w.wexpense.vaadin7.ValidationHelper;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.ui.Notification;


public class ValidateStep<T> extends SavingStep<T> {
    
    public ValidateStep(EditorView<T, ?> view, SavingStep<T> nextStep) {
        super(view, nextStep);
    }
    
    @Override
    public void save(T t) {
        String errorMsg = ValidationHelper.validate(t);
        if (errorMsg != null) {
            Notification.show("Error", errorMsg, Notification.Type.ERROR_MESSAGE);
            
        } else {        
            nextStep(t);
        }
    } 
}
