package w.wexpense.vaadin7.saver;

import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.ui.Notification;

public class NotifyStep<T> extends SavingStep<T> {
    
    public NotifyStep(EditorView<T, ?> view, SavingStep<T> nextStep) {
        super(view, nextStep);
    }
    
    @Override
    public void save(T t) {
        Notification.show("saved...", Notification.Type.TRAY_NOTIFICATION);
        
        nextStep(t);
    }

}
