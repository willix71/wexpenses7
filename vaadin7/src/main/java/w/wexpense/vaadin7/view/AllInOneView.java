package w.wexpense.vaadin7.view;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;

import ru.xpoft.vaadin.VaadinView;
import w.wexpense.model.Expense;
import w.wexpense.model.Template;
import w.wexpense.persistence.dao.ITemplateJpaDao;
import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.event.EntityChangeEvent;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = AllInOneView.NAME, cached = true)
public class AllInOneView extends Panel implements View {

    public static final String NAME = "allInOne";

    private static final long serialVersionUID = 6406409460600093055L;

    private HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
    private VerticalLayout layout = new VerticalLayout();
    private MenuBar menuBar = new MenuBar();
    private Tree navTree = new Tree();

    @Autowired
    private transient ApplicationContext applicationContext;

    @PostConstruct
    public void PostConstruct() {
        addView("adminView", addView("currencyListView"), addView("countryListView"), addView("payeeTypeListView"), addView("expenseTypeListView"),
                addView("templateListView"));
        addView("cityListView");
        addView("payeeListView");
        addView("accountListView");
        addView("discriminatorListView");
        addView("exchangeRateListView");
        addView("expenseListView", addView("todaysExpenseListView"));
        addView("transactionLineListView");
        addView("paymentListView");
        addView("consolidationListView");

        navTree.setSelectable(true);
        navTree.setImmediate(true);
        navTree.setNullSelectionAllowed(false);
        navTree.addValueChangeListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                Panel cv = (Panel) event.getProperty().getValue();
                cv.setSizeFull();
                horizontalSplitPanel.setSecondComponent(cv);
                horizontalSplitPanel.markAsDirty();
            }
        });

        horizontalSplitPanel.setSplitPosition(200, HorizontalSplitPanel.Unit.PIXELS);
        horizontalSplitPanel.addComponent(navTree);
        horizontalSplitPanel.setSizeFull();

        menuBar = new MenuBar();
        buildMenu(menuBar);
        menuBar.setWidth("100%");

        layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(menuBar);
        layout.addComponent(horizontalSplitPanel);
        layout.setExpandRatio(horizontalSplitPanel, 100);

        setContent(layout);
    }

    public Object addView(String name, Object... children) {
        return addView(applicationContext.getBean(name, WindowView.class), children);
    }

    public Object addView(WindowView view, Object... children) {
        navTree.addItem(view);
        navTree.setItemCaption(view, view.getViewName());

        if (children != null && children.length > 0) {
            navTree.setChildrenAllowed(view, true);
            for (Object child : children) {
                navTree.setParent(child, view);
            }
        } else {
            navTree.setChildrenAllowed(view, false);
        }

        return view;
    }

    private class TemplateMenu implements MenuBar.Command {
        private static final long serialVersionUID = 1L;
        
        private Template template;

        public TemplateMenu(Template template) {
            this.template = template;
        }

        @Override
        public void menuSelected(MenuItem selectedItem) {
            Expense x = template.toExpense();
            final EditorView xeditor = ((WexUI) UI.getCurrent()).getBean(EditorView.class, "expenseEditorView");
            xeditor.setInstance(x);
            xeditor.addListener(new Component.Listener() {
                private static final long serialVersionUID = 8121179082149508635L;

                @Override
                public void componentEvent(Event event) {
                    if (event instanceof EntityChangeEvent && event.getComponent() == xeditor) {
                        Component view = horizontalSplitPanel.getSecondComponent();

                        if (view instanceof ListView) {
                            ((ListView) view).refresh();
                        }
                    }
                }
            });
            UIHelper.displayWindow(xeditor);
        }

    }

    public void buildMenu(MenuBar menuBar) {
        Map<String, MenuItem> menus = new HashMap<String, MenuItem>();

        // bypass service
        ITemplateJpaDao dao = applicationContext.getBean(ITemplateJpaDao.class);
        for (Template template : dao.findAll(new Sort(new Sort.Order("templateMenu"), new Sort.Order("templateOrder")))) {
            if (template.getTemplateOrder() == null)
                continue;

            if (template.getTemplateMenu() == null) {
                menuBar.addItem(template.getTemplateName(), new TemplateMenu(template));
            } else {
                MenuItem mnu = menus.get(template.getTemplateMenu());
                if (mnu == null) {
                    // build the menu
                    for (String s : template.getTemplateMenu().split("\\.")) {
                        if (mnu == null) {
                            mnu = menuBar.addItem(s, null);
                        } else {
                            mnu = mnu.addItem(s, null);
                        }
                    }
                    menus.put(template.getTemplateMenu(), mnu);
                }
                mnu.addItem(template.getTemplateName(), new TemplateMenu(template));
            }
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        setSizeFull();
    }
}
