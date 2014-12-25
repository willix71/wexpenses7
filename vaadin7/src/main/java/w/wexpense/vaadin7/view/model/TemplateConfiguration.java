package w.wexpense.vaadin7.view.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Expense;
import w.wexpense.model.Template;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.menu.EnabalebalMenuBar;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.ListView;

import com.vaadin.ui.UI;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

@Configuration
public class TemplateConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateConfiguration.class);
	
	@Autowired
	@Qualifier("templateService")
	private StorableService<Template, String> templateService;
	
	@Bean
	@Scope("prototype")
	public EditorView<Template, String> templateEditorView() {
	    PropertyFieldLayout l = PropertyFieldHelper.getDBableFormPropertyFieldLayout(
	        "createdTs","modifiedTs","templateMenu","templateName","templateDescription","templateOrder","type",
            "payee","amount","currency","externalReference","description","outAccount","outFactor","inAccount","inFactor");
	    
		EditorView<Template, String> editorview = new EditorView<Template, String>(templateService, l);
		initMenuItems(editorview);
		return editorview;
	}
	
	@Bean
	@Scope("prototype")
	public ListView<Template> templateListView() {
		ListView<Template> listview = new ListView<Template>(Template.class);
		listview.setColumnConfigs(
            new TableColumnConfig("id").rightAlign().collapse(),
            new TableColumnConfig("uid").collapse(),
            new TableColumnConfig("createdTs").collapse(),
            new TableColumnConfig("modifiedTs").collapse(),
            new TableColumnConfig("templateMenu","Menu").desc(),
            new TableColumnConfig("templateOrder","Order").asc(),
            new TableColumnConfig("templateName", "Name"),
            new TableColumnConfig("templateDescription", "Description"),
            new TableColumnConfig("type").centerAlign(),
            new TableColumnConfig("amount").rightAlign(),
            new TableColumnConfig("currency").centerAlign(),
            new TableColumnConfig("payee").sortBy(".display").expand(1.0f),              
            new TableColumnConfig("externalReference").collapse(),
            new TableColumnConfig("outAccount"),
            new TableColumnConfig("inAccount")
            );
		ActionHelper.setDefaultListViewActions(listview, "templateEditorView");
		return listview;
	}
	
	private void initMenuItems(final EditorView<Template, String> editorview) {
		EnabalebalMenuBar<Template> menuBar = editorview.getMenuBar();
		MenuItem mnuDta = menuBar.addItem("Template", null);
		mnuDta.addItem("to expense", new Command() {
         @SuppressWarnings("unchecked")
         public void menuSelected(MenuItem selectedItem) { 
         	try {
         		Expense x = editorview.getInstance().toExpense();
         		
         		@SuppressWarnings("rawtypes")
               EditorView xeditor = ((WexUI) UI.getCurrent()).getBean(EditorView.class, "expenseEditorView");
         		xeditor.setInstance(x);
         		UIHelper.displayWindow(xeditor);
         		
         		editorview.close();
         	} catch(Exception e) {
         		LOGGER.error("Failed to convert template to expense", e);
         		throw new RuntimeException(e);
         	}
         }
      });
	}
}
