package w.wexpense.vaadin7.saver;

import w.wexpense.vaadin7.ValidationHelper;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.validation.Warning;

import com.vaadin.ui.Notification;


public class ValidateWarningsStep<T> extends SavingStep<T> {
    
    private long lastWarningTs = 0;
    
    public ValidateWarningsStep(EditorView<T, ?> view, SavingStep<T> nextStep) {
        super(view, nextStep);
    }
    
    @Override
    public void save(T t) {
        // check their are no warnings or that we havn't displayed 
        // the warnings in the last 5 seconds        
        String warningMsg = ValidationHelper.validate(t, Warning.class);
        long currentWarningTs = System.currentTimeMillis();
        if (warningMsg != null && (currentWarningTs - lastWarningTs > 5000)) {
            Notification.show("Warning", warningMsg, Notification.Type.WARNING_MESSAGE);
            lastWarningTs = currentWarningTs;
        } else {        
            nextStep(t);
        }
    } 
}
