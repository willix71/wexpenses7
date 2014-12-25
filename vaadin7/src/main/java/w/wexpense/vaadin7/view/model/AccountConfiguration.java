package w.wexpense.vaadin7.view.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Account;
import w.wexpense.service.model.IAccountService;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.action.ListViewAction;
import w.wexpense.vaadin7.component.RelationalFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;

@Configuration
public class AccountConfiguration {

	@Autowired
	@Qualifier("accountService")
	private IAccountService accountService;
	
	@Bean
	@Scope("prototype")
	public EditorView<Account, Long> accountEditorView() {
	    PropertyFieldLayout l = PropertyFieldHelper.getDBableFormPropertyFieldLayout("parent","name","fullName","number","fullNumber","type","externalReference","owner","currency","selectable");
	    
        EditorView<Account, Long> editorview = new EditorView<Account, Long>(accountService, l) {
            private static final long serialVersionUID = 1L;

            @Override
            public void setInstance(Account t) {
                super.setInstance(t);
                final BeanFieldGroup<Account> bfg = super.fieldGroup;

                Property.ValueChangeListener listener = new Property.ValueChangeListener() {
                    private static final long serialVersionUID = -1;

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        bfg.getItemDataSource().getBean().updateFullNameAndNumber();
                        fieldLayout.getLayout().markAsDirty();
                        bfg.getField("fullName").markAsDirty();
                        bfg.getField("fullNumber").markAsDirty();
                    }
                };

                Field<?> f = bfg.getField("name");
                ((AbstractComponent) f).setImmediate(true);
                f.setBuffered(false);
                f.addValueChangeListener(listener);

                f = bfg.getField("number");
                ((AbstractComponent) f).setImmediate(true);
                f.setBuffered(false);
                f.addValueChangeListener(listener);

                f = bfg.getField("parent");
                ((AbstractComponent) f).setImmediate(true);
                f.setBuffered(false);
                f.addValueChangeListener(listener);
            }
		};
		editorview.setRelationalFieldCustomizers(RelationalFieldHelper.accountCustomisers);
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<Account> accountListView() {
		ListView<Account> listview = new ListView<Account>(Account.class);
		listview.setColumnConfigs(
			   new TableColumnConfig("id").collapse().rightAlign(),
			   new TableColumnConfig("uid").collapse(),
			   new TableColumnConfig("createdTs").collapse(),
			   new TableColumnConfig("modifiedTs").collapse(),

			   new TableColumnConfig("name"),
			   new TableColumnConfig("fullName"),
			   new TableColumnConfig("number"),
			   new TableColumnConfig("fullNumber").asc(),
			   new TableColumnConfig("type"),
			   new TableColumnConfig("currency.code"),
			   new TableColumnConfig("externalReference").collapse(),
			   new TableColumnConfig("owner.display").collapse()			   
			   );
	   
        ActionHelper.setDefaultListViewActions(listview, "accountEditorView", new ListViewAction("renumber") {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void handleAction(final Object sender, final Object target) {
                accountService.renumberAccounts((Long) target);

                com.vaadin.data.Container c = ((Table) sender).getContainerDataSource();
                ((JPAContainer<?>) c).refresh();
            }

            @Override
            public boolean canHandle(Object target, Object sender) {
                return true;
            }
        });
		return listview;
	}
}
