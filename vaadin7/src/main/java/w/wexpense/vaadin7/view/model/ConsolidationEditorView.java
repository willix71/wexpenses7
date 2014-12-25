package w.wexpense.vaadin7.view.model;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import w.wexpense.model.Account;
import w.wexpense.model.Consolidation;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.model.IConsolidationService;
import w.wexpense.utils.TransactionLineUtils;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.ActionHelper;
import w.wexpense.vaadin7.action.AddMultiSelectionAction;
import w.wexpense.vaadin7.action.EditConsolidationTransactionLineAction;
import w.wexpense.vaadin7.action.ListViewAction;
import w.wexpense.vaadin7.action.RemoveAction;
import w.wexpense.vaadin7.component.OneToManyField;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.converter.StringToDateConverter;
import w.wexpense.vaadin7.filter.FilterHelper;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.MultiSelectorView;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;
import com.vaadin.ui.Table;

@org.springframework.stereotype.Component
@Scope("prototype")
public class ConsolidationEditorView extends EditorView<Consolidation, Long> {

    private static final long serialVersionUID = 1L;

    @Autowired
	private EditConsolidationTransactionLineAction editConsolidationTransactionLineAction;
	
	private class AddMultiTransactionLinesAction extends AddMultiSelectionAction<TransactionLine> {
	    private static final long serialVersionUID = 1L;
	    
		public AddMultiTransactionLinesAction(boolean resetMode) {
			super("consolidationTransactionLineSelectorView", resetMode);			
		} 
		
		@Override
		public void prepareSelector(MultiSelectorView<TransactionLine> selector, OneToManyContainer<TransactionLine> container, boolean resetMode) {
			Filter f = FilterHelper.and(getAccountFilter(),ActionHelper.parentFilter("consolidation", ConsolidationEditorView.this.getInstance()));
			
			if (!resetMode && !container.isEmpty()) {
				f = new And(f, ActionHelper.excludeFilter(container.getBeans()));
			}
			selector.setFilter(f);
			
			if (resetMode && !container.isEmpty()) {
				selector.setValues(container.getBeans());
			}
      }			
	};
		
	private OneToManyField<TransactionLine> consolidationTransactionsField;
	
	private IConsolidationService consolidationService;
	
	@Autowired
	public ConsolidationEditorView(IConsolidationService consolidationService) {
	   super(consolidationService, PropertyFieldHelper.getDBableFormPropertyFieldLayout("date","institution","openingBalance","closingBalance","deltaBalance","transactions"));
	   this.consolidationService = consolidationService;
	}
	
	@Override
	public void postConstruct() {
		super.postConstruct();
		
		consolidationTransactionsField = initConsolidationTransactionsField();

		this.setCustomField("transactions",  consolidationTransactionsField);
	}
	
    @Override
    public void initFields() {
        super.initFields();
        Property.ValueChangeListener listener = new Property.ValueChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                updateDelta();
            }
        };
        fieldGroup.getField("openingBalance").addValueChangeListener(listener);
        fieldGroup.getField("closingBalance").addValueChangeListener(listener);
    }

    private OneToManyField<TransactionLine> initConsolidationTransactionsField() {
        final OneToManyField<TransactionLine> tlField = new OneToManyField<TransactionLine>(TransactionLine.class, super.persistenceService,
                ConsolidationConfiguration.getTransactionLinesTableColumnConfig(new ConsolidationStringToDateConverter("dd.MM.yyyy", "dd.MM.yyyy (SS)")));

        ActionHandler action = new ActionHandler();
        action.addListViewAction(new AddMultiTransactionLinesAction(true));
        action.addListViewAction(new AddMultiTransactionLinesAction(false));
        action.addListViewAction(editConsolidationTransactionLineAction);
        action.addListViewAction(new RemoveAction<TransactionLine>());
        action.addListViewAction(new ConsolidationDateEditor("up", -1));
        action.addListViewAction(new ConsolidationDateEditor("down", 1));
        tlField.setActionHandler(action);

        // set footer
        tlField.addFooterListener(new Container.ItemSetChangeListener() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void containerItemSetChange(ItemSetChangeEvent event) {
                @SuppressWarnings("unchecked")
                OneToManyContainer<TransactionLine> otmContainer = (OneToManyContainer<TransactionLine>) event.getContainer();
                Collection<TransactionLine> tls = otmContainer.getBeans();
                BigDecimal[] values = TransactionLineUtils.getValueDeltaAndTotals(tls);
                tlField.setFooter("outValue", MessageFormat.format("{0,number,0.00}", values[1]));
                tlField.setFooter("inValue", MessageFormat.format("{0,number,0.00}", values[2]));
                tlField.setFooter("payee", MessageFormat.format("{0,number,0} X", tls.size()));

                updateDelta(values[0]);
            }
        });

        return tlField;
    }
	
	private void updateDelta() {
		BigDecimal[] values = TransactionLineUtils.getValueDeltaAndTotals(getInstance().getTransactions());
		updateDelta(values[0]);	
	}
	
	private void updateDelta(BigDecimal delta) {
		getInstance().updateDeltaBalance(delta);				
		fieldGroup.getField("deltaBalance").markAsDirty();
	}
	
	private Filter getAccountFilter() {
		List<Account> accounts = consolidationService.getConsolidationAccounts(getInstance().getInstitution());
		if (accounts != null && accounts.size() > 0) {
			List<Filter> filters = new ArrayList<Filter>();
			for(Account a: accounts) {
				filters.add(new Compare.Equal("account", a));
			}
			return new Or(filters.toArray(new Filter[filters.size()]));
		}
		
		return null;
	}
	
   class ConsolidationStringToDateConverter extends StringToDateConverter {        
        private static final long serialVersionUID = 1L;
        
        private DateFormat df2;
        
        public ConsolidationStringToDateConverter(String formatWithoutMillis, String formatWithMillis) {
            super(formatWithoutMillis);
            df2 = new SimpleDateFormat(formatWithMillis);
        }
        
        @Override
        public Date convertToModel(String value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
            if (value == null || value.length() == 0) return null;

            try {
                return df2.parse(value);
            } catch (ParseException e) {
                return super.convertToModel(value, locale);
            }
        }

        @Override
        public synchronized String convertToPresentation(Date value, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
            if (value == null)  return null;

            if (value.getTime() % 1000 > 0) {
                return df2.format(value); 
            } else {
                return super.convertToPresentation(value, locale);
            }
        }
    }
   
   class ConsolidationDateEditor extends ListViewAction {

       private static final long serialVersionUID = 1L;
       
       private int increase;

       public ConsolidationDateEditor(String name, int increase) {
           super(name);
           this.increase = increase;
       }   

       @Override
       public void handleAction(final Object sender, final Object target) {
           if (target != null) {
               com.vaadin.data.Container c = ((Table) sender) .getContainerDataSource();
               
               @SuppressWarnings("unchecked")
               BeanItem<TransactionLine> item = (BeanItem<TransactionLine>) c.getItem(target);
               
               Property<Date> pd = item.getItemProperty("date");
               Calendar cal = Calendar.getInstance();
               cal.setTime(pd.getValue());
               
               int millis = cal.get(Calendar.MILLISECOND) + increase;
               if (millis >= 0 && millis < 1000) {
                   cal.set(Calendar.MILLISECOND, millis);
               }
               pd.setValue(cal.getTime());
               
               ((Table) sender).sort(new Object[]{"date"}, new boolean[]{true});
               System.out.println("using jrebel 8");
           }

       }
       @Override
       public boolean canHandle(Object target, Object sender) {
           return target != null;
       }
   }
}
