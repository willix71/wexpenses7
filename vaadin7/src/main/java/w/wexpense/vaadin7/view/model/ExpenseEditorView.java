package w.wexpense.vaadin7.view.model;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.service.model.IExpenseService;
import w.wexpense.utils.PaymentDtaUtils;
import w.wexpense.utils.TransactionLineUtils;
import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.WexUI;
import w.wexpense.vaadin7.action.ActionHandler;
import w.wexpense.vaadin7.action.ListViewAction;
import w.wexpense.vaadin7.action.RemoveAction;
import w.wexpense.vaadin7.component.ExchangeRateField;
import w.wexpense.vaadin7.component.OneToManyField;
import w.wexpense.vaadin7.component.WexDateField;
import w.wexpense.vaadin7.container.OneToManyContainer;
import w.wexpense.vaadin7.event.FieldCreationEvent;
import w.wexpense.vaadin7.layout.PropertyFieldConfig;
import w.wexpense.vaadin7.layout.PropertyFieldHelper;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.layout.StaticFieldConfig;
import w.wexpense.vaadin7.saver.CheckSimilarExpenseStep;
import w.wexpense.vaadin7.saver.CheckWarningsStep;
import w.wexpense.vaadin7.saver.NotifyStep;
import w.wexpense.vaadin7.saver.SaveStep;
import w.wexpense.vaadin7.saver.SavingStep;
import w.wexpense.vaadin7.saver.ValidateStep;
import w.wexpense.vaadin7.support.TableColumnConfig;
import w.wexpense.vaadin7.view.EditorView;

@org.springframework.stereotype.Component
@Scope("prototype")
public class ExpenseEditorView extends EditorView<Expense, Long> {

    private static final long serialVersionUID = 1L;

    private static class DatePart extends StaticFieldConfig<Expense> {
        Label datePart = new Label();
        String messageFormat;
        public DatePart(AbstractLayout layout, String caption, String messageFormat) {
            super(layout, null);
            datePart.setCaption(caption);
            this.messageFormat = messageFormat;
        }

        @Override
        public void configure(BeanFieldGroup<Expense> fieldGroup) {
            super.configure(datePart);
            ((WexDateField) fieldGroup.getField("date")).addValueChangeListener(new ValueChangeListener() {                
                private static final long serialVersionUID = 1L;
                @Override
                public void valueChange(ValueChangeEvent event) {
                    formatDayOfWeek((Date) event.getProperty().getValue());                        
                }
            });
        }

        @Override
        public void configure(BeanFieldGroup<Expense> fieldGroup, Expense instance) {
            formatDayOfWeek(instance.getDate());
        }
        
        void formatDayOfWeek(Date d) {
            if (d == null) {
                datePart.setValue("");
            } else {
                datePart.setValue(MessageFormat.format(messageFormat, d)); 
            }
        }
        
        @Override
        public boolean isReadOnly() { return true; }
    }
        
    private static PropertyFieldLayout getGridPropertyFieldLayout() {
        VerticalLayout vl = PropertyFieldHelper.getVerticalLayout(false, true);
        
        PropertyFieldLayout fpl = new PropertyFieldLayout(vl);
        
        HorizontalLayout hl = PropertyFieldHelper.getHorizontalLayout(false, false);
      
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl, "fullId").width(100, Unit.PIXELS));
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl, "uid").width(300, Unit.PIXELS));
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl, "createdTs"));
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl, "modifiedTs"));
        fpl.addPropertyFieldConfig(new StaticFieldConfig<Expense>(vl, hl));
        
        HorizontalLayout hl2 = PropertyFieldHelper.getHorizontalLayout(true, false);
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl2, "type").width(200, Unit.PIXELS));
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl2, "payment").expand(100f));
        fpl.addPropertyFieldConfig(new StaticFieldConfig<Expense>(vl, hl2).width(100, Unit.PERCENTAGE));

        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(vl, "payee"));
        
        // date/time
        HorizontalLayout hl31 = PropertyFieldHelper.getHorizontalLayout(true, false);    
        fpl.addPropertyFieldConfig(new DatePart(hl31, "Day", "{0,date,EE}").width(40,Unit.PIXELS));
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl31, "date").width(140, Unit.PIXELS));
        fpl.addPropertyFieldConfig(new DatePart(hl31, "Time", "{0,date,HH:mm}").width(60,Unit.PIXELS));
        
        // amount
        HorizontalLayout hl32 = PropertyFieldHelper.getHorizontalLayout(true, false);
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl32, "amount").width(180, Unit.PIXELS));
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl32, "currency").width(60, Unit.PIXELS));
        fpl.addPropertyFieldConfig(new StaticFieldConfig<Expense>(vl, hl32));
        
        VerticalLayout vl30 = PropertyFieldHelper.getVerticalLayout(false, false);
        fpl.addPropertyFieldConfig(new StaticFieldConfig<Expense>(vl30, hl31));
        fpl.addPropertyFieldConfig(new StaticFieldConfig<Expense>(vl30, hl32));        
        
        // payed
        HorizontalLayout hl3 = PropertyFieldHelper.getHorizontalLayout(true, false);
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl3, "payed").width(100,Unit.PERCENTAGE).height(4,Unit.PICAS).readOnly(true));
        fpl.addPropertyFieldConfig(new StaticFieldConfig<Expense>(hl3, vl30).width(250, Unit.PIXELS));
        
        fpl.addPropertyFieldConfig(new StaticFieldConfig<Expense>(vl, hl3).width(100, Unit.PERCENTAGE));
        
        
        
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(vl, "externalReference"));
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(vl, "description"));
        
        HorizontalLayout hl4 = PropertyFieldHelper.getHorizontalLayout(true, false);
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl4, "fileDate").width(200, Unit.PIXELS));
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(hl4, "fileName").expand(100f));
        fpl.addPropertyFieldConfig(new StaticFieldConfig<Expense>(vl, hl4).width(100, Unit.PERCENTAGE));
        
        fpl.addPropertyFieldConfig(new PropertyFieldConfig<Expense>(vl, "transactions"));        
        return fpl;
    }
    
    private OneToManyField<TransactionLine> expenseTransactionsField;
	
	private BigDecimal oldAmount;
	
	private MenuItem unlock;
	
	private Property.ValueChangeListener amountListener = new Property.ValueChangeListener() {
	   private static final long serialVersionUID = 1L;
	   
		@Override
		public void valueChange(Property.ValueChangeEvent event) {
			updateAmount();
		}
	};
	
	private List<ExchangeRateField> exchangeRateFields = new ArrayList<ExchangeRateField>();
	private com.vaadin.ui.Component.Listener exchangeRateListener = new com.vaadin.ui.Component.Listener() {
	   private static final long serialVersionUID = 1L;
	   
		@Override
		public void componentEvent(com.vaadin.ui.Component.Event event) {			
			updateExchangeRate(((ExchangeRateField) event.getSource()).getValue());
		}
	};
	
	@Autowired
	public ExpenseEditorView(IExpenseService expenseService) {
	   super(expenseService, getGridPropertyFieldLayout());
	   
	   MenuItem adminMenu = menuBar.addItem("admin", null);
	   unlock = menuBar.addItem(adminMenu, "unlock", null, new Command() {
	        private static final long serialVersionUID = 1L;
	      
           public void menuSelected(MenuItem selectedItem) {
              lock(false);
              unlock.setEnabled(false);
           }
	   });
	}
	
	@Override
	public void postConstruct() {
		super.postConstruct();

		expenseTransactionsField = initExpenseTransactionsField();
		expenseTransactionsField.setActionHandler(initExpenseTransactionsActionHanler());
		expenseTransactionsField.addListener(new com.vaadin.ui.Component.Listener() {
			private static final long serialVersionUID = 8121179082149508635L;

			@Override
			public void componentEvent(Event event) {
				if (event instanceof FieldCreationEvent && event.getComponent() == expenseTransactionsField) {
					FieldCreationEvent fcevnt = (FieldCreationEvent) event;
					if ("amount".equals(fcevnt.getColId())) {
						((AbstractComponent) fcevnt.getField()).setImmediate(true);
						fcevnt.getField().addValueChangeListener(amountListener);
					} else if ("exchangeRate".equals(fcevnt.getColId()) && fcevnt.getField() instanceof ExchangeRateField) {
						ExchangeRateField xRateField = (ExchangeRateField) fcevnt.getField();
						exchangeRateFields.add(xRateField);
						fcevnt.getField().addListener(exchangeRateListener); 
						xRateField.setOwner((TransactionLine) fcevnt.getRowId());
					} else if (("factor").equals(fcevnt.getColId())) {
						((AbstractComponent) fcevnt.getField()).setImmediate(true);
						fcevnt.getField().addValueChangeListener(new Property.ValueChangeListener() {
						   private static final long serialVersionUID = 1L;
						   
							@Override
							public void valueChange(Property.ValueChangeEvent event) {
								expenseTransactionsField.itemChange();
							}
						});
					}
				}
			}
		});    
		
		this.setCustomField("transactions", expenseTransactionsField);
		
		TextArea payedField = new TextArea(); 
		payedField.setNullRepresentation("");
		this.setCustomField("payed", payedField);
   }
	
    @Override
	public void initFields() {
        super.initFields();
        
		UIHelper.addValueChangeListener(getField("amount"), amountListener);
		
		UIHelper.addValueChangeListener(getField("date"), new Property.ValueChangeListener() {
		   private static final long serialVersionUID = 1L;
		   
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				updateTransactionLineDate();
			}
		});
	
		UIHelper.addValueChangeListener(getField("payee"),new Property.ValueChangeListener() {
		   private static final long serialVersionUID = 1L;
		   
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				Payee payee = getInstance().getPayee();
				
				String payed = null;
				try {
					payed = PaymentDtaUtils.getDtaFormater(getInstance()).payee(getInstance());
				} catch(Exception e) {
					payed = payee==null?null:payee.toShortString();
				}
				getInstance().setPayed(payed);
				getField("payed").markAsDirty();
				
				if (payee.getCity()!=null && payee.getCity().getCountry()!=null) {
					getInstance().setCurrency(payee.getCity().getCountry().getCurrency());
					getField("currency").markAsDirty();
				}
			}
		});
		
		UIHelper.addValueChangeListener(getField("fileDate"), new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				Expense x = getInstance();
				int uidlen = x.getUid().length();
				String fileName = MessageFormat.format(
						"{0,date,yyyyMMdd} {1,number,0.00}{2} {3}.{4}.pdf",
						x.getFileDate(), 
						x.getAmount(), 
						x.getCurrency().getCode(), 
						x.getPayee().getPrefixedName().replaceAll("\\s+", "_"),
						x.getUid().substring(uidlen-4,uidlen));
				x.setFileName(fileName);
				getField("fileName").markAsDirty();
			}
		});
	}
	
    @Override
    public void initFields(Expense x) {
        super.initFields(x);
        boolean enabled = x.getPayment()==null || x.getPayment().isSelectable();
        lock(!enabled);
        unlock.setEnabled(!enabled);
    }
    
    protected void lock(boolean locked) {
        fieldGroup.getField("type").setReadOnly(locked);
        fieldGroup.getField("payment").setReadOnly(locked);
        fieldGroup.getField("payee").setReadOnly(locked);
        fieldGroup.getField("date").setReadOnly(locked);
        fieldGroup.getField("amount").setReadOnly(locked);
        fieldGroup.getField("currency").setReadOnly(locked);
        fieldGroup.getField("externalReference").setReadOnly(locked);
    }
    
	@Override
   public void setInstance(Expense expense) {
      super.setInstance(expense);
      oldAmount = expense.getAmount();
   }

	@Override
	 protected SavingStep<Expense> getSavingSteps(SavingStep<Expense> finalizer) {
           return new ValidateStep<Expense>(this, new CheckWarningsStep<Expense>(this, 
                   new CheckSimilarExpenseStep(this, new SaveStep<Expense>(this, new NotifyStep<Expense>(this, finalizer) ) ) ) );
	 }
	   
	private void updateExchangeRate(ExchangeRate newExchangeRate) {
		Expense x = getInstance();
		for (TransactionLine line : x.getTransactions()) {
			if (newExchangeRate!=null && newExchangeRate.equals(line.getExchangeRate())) {
				line.setExchangeRate(newExchangeRate);
			}
		}
		updateAmount() ;
	}
	
	private void updateAmount() {
		Expense x = getInstance();
		if (x.getTransactions() != null) {
			TransactionLineUtils.updateAmount(x.getTransactions(), x.getAmount(), oldAmount);
			expenseTransactionsField.itemChange();
		}
		oldAmount = x.getAmount();
	}
	
	private void updateTransactionLineDate() {
		Expense x = getInstance();
		Date xd = x.getDate();
		if (x.getTransactions() != null) {
			for(TransactionLine line: x.getTransactions()) {
				line.setDate(xd);
			}
		}
		expenseTransactionsField.itemChange();
	}
	
	private OneToManyField<TransactionLine> initExpenseTransactionsField() {
      final OneToManyField<TransactionLine> xtransactionsField = new OneToManyField<TransactionLine>(
      		TransactionLine.class, super.persistenceService, 
            new TableColumnConfig("fullId").collapse().rightAlign(),
            new TableColumnConfig("uid").collapse(),
            new TableColumnConfig("createdTs").collapse(),
            new TableColumnConfig("modifiedTs").collapse(),
           
            new TableColumnConfig("account").expand(.5f),
            new TableColumnConfig("discriminator").expand(.5f),
            new TableColumnConfig("factor").centerAlign().width(60),
            new TableColumnConfig("amount").rightAlign().width(80),
            new TableColumnConfig("exchangeRate", "rate").width(100),
            new TableColumnConfig("value").rightAlign().width(80)
      		);
      xtransactionsField.setPageLength(4);
      xtransactionsField.setEditable(true);
		xtransactionsField.addFooterListener(new Container.ItemSetChangeListener() {
		   private static final long serialVersionUID = 1L;
		   
			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				@SuppressWarnings("unchecked")
				OneToManyContainer<TransactionLine> otmContainer = (OneToManyContainer<TransactionLine>) event.getContainer();
				BigDecimal[] values = TransactionLineUtils.getAmountDeltaAndTotals(otmContainer.getBeans());
				if (values[1].equals(values[2])) {
					xtransactionsField.setFooter("amount", MessageFormat.format("{0,number,0.00}",values[1]));
				} else {
					xtransactionsField.setFooter("amount", MessageFormat.format("Diff {0,number,0.00}",values[0]));
				}
			}
		});
		return xtransactionsField;
	}
	
	private ActionHandler initExpenseTransactionsActionHanler() {
		// set the expenseTransactionsField menu actions
		ListViewAction addAction = new ListViewAction("add") {	
         private static final long serialVersionUID = 1L;

         @Override
			public void handleAction(Object sender, Object target) {
				Table table = (Table) sender;
				
				@SuppressWarnings("unchecked")
            OneToManyContainer<TransactionLine> container = (OneToManyContainer<TransactionLine>) table.getContainerDataSource();
				Collection<TransactionLine> lines = container.getBeans();
				
				TransactionLine tl = TransactionLineUtils.newTransactionLine(getInstance(), lines);
				container.addBean(tl);				
			}
			
			@Override
			public boolean canHandle(Object target, Object sender) {
				return true;
			}
		};
      
		ListViewAction editAction = new ListViewAction("edit") {	
		   private static final long serialVersionUID = 1L;
		   
			@Override
			public void handleAction(Object sender, Object target) {
				if (target != null) {
					Table table = (Table) sender;
					
					@SuppressWarnings("unchecked")
					OneToManyContainer<TransactionLine> container = (OneToManyContainer<TransactionLine>) table.getContainerDataSource();
					
					BeanItem<TransactionLine> item = container.getItem(target);
					TransactionLineEditorView editor = ((WexUI) UI.getCurrent()).getBean(TransactionLineEditorView.class, "transactionLineEditorView");
					editor.setBeanItem(item);					
					UIHelper.displayModalWindow(editor);
				}
			}
			@Override
			public boolean canHandle(Object target, Object sender) {
				return target != null;
			}
		};
      ActionHandler actionHandler = new ActionHandler();
      actionHandler.addListViewAction(addAction);
      actionHandler.addListViewAction(editAction);
      actionHandler.addListViewAction(new RemoveAction<TransactionLine>());
      return actionHandler;
	}
}
