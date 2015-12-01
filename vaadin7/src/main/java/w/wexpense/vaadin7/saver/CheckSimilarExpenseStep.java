package w.wexpense.vaadin7.saver;

import java.util.List;

import org.vaadin.dialogs.ConfirmDialog;

import w.wexpense.model.Expense;
import w.wexpense.service.model.IExpenseService;
import w.wexpense.vaadin7.view.EditorView;

import com.vaadin.ui.UI;

public class CheckSimilarExpenseStep extends SavingStep<Expense> {

    public CheckSimilarExpenseStep(EditorView<Expense, Long> view, SavingStep<Expense> nextStep) {
        super(view, nextStep);
    }
    
    @Override
    public void save(final Expense x) {

        List<Expense> sims = ((IExpenseService) getEditorView().getStoreService()).findSimiliarExpenses(x);
        if (sims == null || sims.size() == 0) {
            nextStep(x);
        } else {

        String similarText = "there are similiar expenses";
        
        for(Expense ex: sims) {
           similarText += "\n" + ex.getPayee();
        }
        
        ConfirmDialog.show(UI.getCurrent(), "Are you sure you want to save?", similarText, "yes", "no", new ConfirmDialog.Listener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                    nextStep(x);
                }
            }
        });
        }
    }
}
