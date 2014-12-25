package w.wexpense.vaadin7.saver;

import java.util.List;

import w.wexpense.vaadin7.view.EditorView;

public class SavingStepHelper {

    public static <T> SavingStep<T> getSavingSteps(EditorView<T, ?> view, List<Class<? extends SavingStep<T>>> steps) {
        try {
            SavingStep<T> previous = null;

            for (Class<? extends SavingStep<T>> step : steps) {
                SavingStep<T> current = step.newInstance();
                current.setEditorView(view);
                if (previous != null) {
                    previous.setNextStep(current);
                }
                previous = current;
            }
            return null;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Saving Steps", e);
        }
    }
}
