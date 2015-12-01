package w.wexpense.vaadin7.view.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import w.log.extras.Log;
import w.wexpense.dta.DtaException;
import w.wexpense.model.Expense;
import w.wexpense.model.Payment;
import w.wexpense.service.model.IPaymentService;
import w.wexpense.utils.PaymentDtaUtils;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.action.AddMultiSelectionAction;
import w.wexpense.vaadin7.action.RemoveAction;
import w.wexpense.vaadin7.component.OneToManyField;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.menu.EnabalebalMenuBar;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.MultiSelectorView;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.And;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@org.springframework.stereotype.Component
@Scope("prototype")
public class PaymentEditorView extends EditorView<Payment, Long> {
    private static final long serialVersionUID = 1L;

    @Log
    private static Logger LOGGER;

    private class AddMultiExpensesAction extends AddMultiSelectionAction<Expense> {
        private static final long serialVersionUID = 1L;

        public AddMultiExpensesAction(boolean resetMode) {
            super("paymentExpenseSelectorView", resetMode);
        }

        @Override
        public void prepareSelector(MultiSelectorView<Expense> selector, OneToManyContainer<Expense> container, boolean resetMode) {
            Filter f = ActionHelper.parentFilter("payment", PaymentEditorView.this.getInstance());
            if (!resetMode && !container.isEmpty()) {
                f = new And(f, ActionHelper.excludeFilter(container.getBeans()));
            }
            selector.setFilter(f);

            if (resetMode && !container.isEmpty()) {
                selector.setValues(container.getBeans());
            }
        }
    };

    private OneToManyField<Expense> expensesField;

    private IPaymentService paymentService;

    @Autowired
    public PaymentEditorView(IPaymentService paymentService) {
        super(paymentService, PropertyFieldHelper.getDBableFormPropertyFieldLayout("date", "filename", "selectable", "expenses"));
        this.paymentService = paymentService;

        this.initMenuItems();
    }

    @Override
    public void postConstruct() {
        super.postConstruct();

        expensesField = initExpensesField();

        this.setCustomField("expenses", expensesField);

    }

    @Override
    public void initFields(Payment payment) {
        super.initFields(payment);
        fieldGroup.getField("selectable").setReadOnly(true);
        fieldGroup.getField("date").setReadOnly(!payment.isSelectable());
        ((OneToManyField<?>) fieldGroup.getField("expenses")).getActionHandler().setEnabled(payment.isSelectable());
    }

    private OneToManyField<Expense> initExpensesField() {
        final OneToManyField<Expense> xField = new OneToManyField<Expense>(Expense.class, super.persistenceService,
                PaymentConfiguration.getExpenseTableColumnConfig());

        ActionHandler expensesActions = new ActionHandler();
        expensesActions.addListViewAction(new AddMultiExpensesAction(true));
        expensesActions.addListViewAction(new AddMultiExpensesAction(false));
        expensesActions.addListViewAction(new RemoveAction<Expense>());
        xField.setActionHandler(expensesActions);

        // set footer
        xField.addFooterListener(new Container.ItemSetChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void containerItemSetChange(ItemSetChangeEvent event) {
                @SuppressWarnings("unchecked")
                OneToManyContainer<Expense> otmContainer = (OneToManyContainer<Expense>) event.getContainer();
                Collection<Expense> xs = otmContainer.getBeans();
                BigDecimal total = new BigDecimal(0);
                for (Expense x : xs) {
                    total = total.add(x.getAmount());
                }
                xField.setFooter("amount", MessageFormat.format("{0,number,0.00}", total));
                xField.setFooter("payee", MessageFormat.format("{0,number,0} X", xs.size()));
            }
        });

        return xField;
    }

    private void initMenuItems() {
        EnabalebalMenuBar<Payment> menuBar = getMenuBar();
        MenuItem mnuDta = menuBar.addItem("DTA", null);
        menuBar.addItem(mnuDta, "clear", this.NEW_DISABLER, new Command() {
            private static final long serialVersionUID = 1L;
            @SuppressWarnings("unchecked")
            public void menuSelected(MenuItem selectedItem) {
                BeanItem<Payment> bp = getBeanItem();
                try {
                    if (!bp.getBean().isSelectable()) {
                        bp.getItemProperty("filename").setValue(Payment.DEFAULT_FILENAME);
                        bp.getItemProperty("selectable").setValue(true);
                        bp.getItemProperty("date").setValue(null);

                        getField("date").setReadOnly(false);
                        (((OneToManyField<?>) getField("expenses"))).getActionHandler().setEnabled(true);
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed clearing DTA for payement " + bp.getItemProperty("uid").getValue(), e);
                    throw new RuntimeException(e);
                }
            }
        });
        menuBar.addItem(mnuDta, "view", this.NEW_DISABLER, new Command() {
            private static final long serialVersionUID = 1L;
            public void menuSelected(MenuItem selectedItem) {
                Payment p = getBeanItem().getBean();
                try {
                    if (p.isSelectable()) {
                        p = generatePaymentDtas(PaymentEditorView.this, p);
                    } else {
                        // need to refresh the instance so as to be able to
                        // follow the DTA's relationship
                        p = paymentService.load(p.getId());
                    }

                    String html = PaymentDtaUtils.getDtaLines(p.getDtaLines(), false);
                    Label label = new Label(html, ContentMode.PREFORMATTED);
                    Window window = new Window();
                    window.setContent(label);
                    window.center();
                    UI.getCurrent().addWindow(window);

                } catch (DtaException dtae) {
                    Notification.show("Error", dtae.getMessage(), Notification.Type.ERROR_MESSAGE);
                } catch (Exception e) {
                    LOGGER.error("Failed viewing " + (p.isSelectable() ? "generated" : "") + " DTA for payement " + p.getUid(), e);
                    throw new RuntimeException(e);
                }
            }
        });
        menuBar.addItem(mnuDta, "save", this.NEW_DISABLER, new Command() {
            private static final long serialVersionUID = 1L;
            public void menuSelected(MenuItem selectedItem) {
                Payment p = getBeanItem().getBean();
                try {
                    if (p.isSelectable()) {
                        p = generatePaymentDtas(PaymentEditorView.this, p);
                    }
                    Page.getCurrent().open("vaadin/download?paymentId=" + p.getId(), "_blank");
                } catch (DtaException dtae) {
                    Notification.show("Error", dtae.getMessage(), Notification.Type.ERROR_MESSAGE);
                } catch (Exception e) {
                    LOGGER.error("Failed saving " + (p.isSelectable() ? "generated" : "") + " DTA for payement " + p.getUid(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Payment generatePaymentDtas(EditorView<Payment, Long> editorview, Payment p) throws DtaException {
        editorview.getField("expenses").commit();

        p = paymentService.generatePaymentDtas(p);

        // this invalidates the payment (why?) so we will have to reload it
        editorview.fireEntityChange(p);

        p = paymentService.load(p.getId());

        Notification.show("Payment DTA generated " + p.getExpenses().size() + " expenses", Notification.Type.TRAY_NOTIFICATION);

        // make sure the editor view is updated with the latest version
        editorview.setInstance(p);

        // return the latest version
        return p;
    }
}
