package w.wexpense.vaadin7.saver;

import org.vaadin.dialogs.ConfirmDialog;

import w.wexpense.vaadin7.ValidationHelper;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.validation.Warning;

import com.vaadin.ui.UI;

public class CheckWarningsStep<T> extends SavingStep<T> {

    public CheckWarningsStep(EditorView<T, ?> view, SavingStep<T> nextStep) {
        super(view, nextStep);
    }

    @Override
    public void save(final T t) {

        String warningMsg = ValidationHelper.validate(t, Warning.class);

        if (warningMsg == null) {
            nextStep(t);
        } else
            ConfirmDialog.show(UI.getCurrent(), "Are you sure you want to save?", warningMsg, "yes", "no", new ConfirmDialog.Listener() {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                        nextStep(t);
                    }
                }
            });
    }
}
