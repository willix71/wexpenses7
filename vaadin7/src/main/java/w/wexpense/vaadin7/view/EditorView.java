package w.wexpense.vaadin7.view;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import w.wexpense.model.Duplicatable;
import w.wexpense.persistence.PersistenceUtils;
import w.wexpense.service.StorableService;
import w.wexpense.vaadin7.component.RelationalFieldCustomizer;
import w.wexpense.vaadin7.component.RelationalFieldFactory;
import w.wexpense.vaadin7.component.RelationalFieldHelper;
import w.wexpense.vaadin7.container.ContainerService;
import w.wexpense.vaadin7.event.EntityChangeEvent;
import w.wexpense.vaadin7.layout.PropertyFieldConfig;
import w.wexpense.vaadin7.layout.PropertyFieldLayout;
import w.wexpense.vaadin7.menu.EnabalebalMenuBar;
import w.wexpense.vaadin7.saver.CheckWarningsStep;
import w.wexpense.vaadin7.saver.CloseWindow;
import w.wexpense.vaadin7.saver.NotifyStep;
import w.wexpense.vaadin7.saver.RefreshView;
import w.wexpense.vaadin7.saver.SaveStep;
import w.wexpense.vaadin7.saver.SavingStep;
import w.wexpense.vaadin7.saver.ValidateStep;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class EditorView<T, ID extends Serializable> extends GenericView<T> {

	private static final long serialVersionUID = 5282517667310057582L;

	public interface AfterSave<T> { void saved(T t); }
	
	public final EnabalebalMenuBar.Enabalebal<T> NEW_DISABLER = new EnabalebalMenuBar.Enabalebal<T> () {
		@Override
      public boolean isEnabled(T t) {
	      return PersistenceUtils.getIdValue(t) != null;
      }	
	};

	@Autowired
	protected ContainerService persistenceService;

	protected StorableService<T, ID> storeService;

	protected boolean readOnly = false;
	
	protected boolean initialized = false;

	protected PropertyFieldLayout fieldLayout;
	
	protected BeanFieldGroup<T> fieldGroup;

	protected Map<String, Field<?>> customFields = new HashMap<>();
	
	protected RelationalFieldCustomizer[] relationalFieldCustomizers = RelationalFieldHelper.defaultCustomisers;
	
	protected EnabalebalMenuBar<T> menuBar;

	private long lastWarningTs = 0;
	
	protected Button[] buttons = new Button[] {
			new Button("Save", new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
				    getSavingSteps(new CloseWindow<T>(EditorView.this)).save(fieldGroup.getItemDataSource().getBean());
				}
			}), new Button("Cancel", new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					cancel();
					close();
				}
			})
	};
	       
	public EditorView(StorableService<T, ID> storeService, PropertyFieldLayout fieldLayout) {
		super(storeService.getEntityClass());

		this.storeService = storeService;
		this.fieldLayout = fieldLayout;
		
		menuBar = new EnabalebalMenuBar<T>();
		menuBar.setWidth("100%");

		MenuItem editMenu = menuBar.addItem("edit", null);		
		menuBar.addItem(editMenu, "save", null, new Command() {
	         public void menuSelected(MenuItem selectedItem) {
	             getSavingSteps(new RefreshView<T>(EditorView.this)).nextStep(fieldGroup.getItemDataSource().getBean());
	         }
	      });
		
		menuBar.addItem(editMenu, "reset", NEW_DISABLER, new Command() {
	         @SuppressWarnings("unchecked")
            public void menuSelected(MenuItem selectedItem) { 
					cancel();
					loadInstance((ID) PersistenceUtils.getIdValue(fieldGroup.getItemDataSource().getBean()));
					Notification.show("reset...", Notification.Type.TRAY_NOTIFICATION);
	         }
	      });
		
		if (Duplicatable.class.isAssignableFrom(storeService.getEntityClass())) {
			menuBar.addItem(editMenu, "duplicate", NEW_DISABLER, new Command() {
	         @SuppressWarnings("unchecked")
            public void menuSelected(MenuItem selectedItem) { 
					setInstance(((Duplicatable<T>) fieldGroup.getItemDataSource().getBean()).duplicate());
					Notification.show("duplicated...", Notification.Type.TRAY_NOTIFICATION);
	         }
	      });
		}
		
		menuBar.addItem(editMenu, "delete", NEW_DISABLER, new Command() {
         public void menuSelected(MenuItem selectedItem) { 
				delete();
         }
      });
		menuBar.addItem(editMenu, "close", null, new Command() {
         public void menuSelected(MenuItem selectedItem) { 
				close();
         }
      });		
	}

//    public EditorView(StorableService<T, ID> storeService, Object ... propertiesId) {
//        this(storeService, new FormPropertyFieldLayout(propertiesId));
//    }
    
	@PostConstruct
	public void postConstruct() {	
		fieldGroup = new BeanFieldGroup<T>(getEntityClass());
		fieldGroup.setBuffered(false);
		
		Label shiftRight = new Label();
		HorizontalLayout buttonLayout = new HorizontalLayout(shiftRight);               
        buttonLayout.setExpandRatio(shiftRight, 1);
        buttonLayout.setWidth(100, Unit.PERCENTAGE);
        buttonLayout.addComponents(buttons);
		
        VerticalLayout layout = new VerticalLayout();
		if (menuBar != null) {
			layout.addComponent(menuBar);
		}
		
		layout.addComponent(fieldLayout.getLayout());
		layout.addComponent(buttonLayout);
		setContent(layout);
	}
    
	public EnabalebalMenuBar<T> getMenuBar() {
		return menuBar;
	}
	
	public T newInstance(Object ...args) {
		T t = storeService.newInstance(args);
		setInstance(t);
		return t;
	}

	public T loadInstance(ID id) {
		T t = storeService.load(id);
		setInstance(t);
		return t;
	}

	public void setInstance(T t) {
		fieldGroup.setReadOnly(readOnly);

		// We need an item data source before we create the fields to be able to
		// find the properties, otherwise we have to specify them by hand
		fieldGroup.setItemDataSource(new BeanItem<T>(t));

		if (!initialized) {
			fieldGroup.setFieldFactory(getFieldGroupFieldFactory());

			// Loop through the properties
			for(Object propertyId: fieldLayout.getIds()) {
				// build fields for them and add the fields to this UI
				Field<?> f = customFields.get(propertyId); 
				if (f != null) {
					f.setCaption(DefaultFieldFactory.createCaptionByPropertyId(propertyId));
					fieldGroup.bind(f, propertyId);
				} else {
					f = fieldGroup.buildAndBind(propertyId);
				}
				// to have a validation straight away
				((AbstractComponent)f).setImmediate(true);
				
			}
			            
            initMenus();
			initFields();
			
			initialized = true;
		} 
		
		initMenus(t);
		initFields(t);
	}
	
	
	protected FieldGroupFieldFactory getFieldGroupFieldFactory() {
		return new RelationalFieldFactory(persistenceService, relationalFieldCustomizers);
	}
	
	public void initMenus() {
		// void
	}
	
	public void initMenus(T t) {
		menuBar.enableMenu(t);
	}
	
	public void initFields() {
        for (PropertyFieldConfig fieldConfig : fieldLayout.getPropertyFieldConfigs()) {
            fieldConfig.configure(fieldGroup);
        }
	}

	public void initFields(T t) {		
        for (PropertyFieldConfig fieldConfig : fieldLayout.getPropertyFieldConfigs()) {
            fieldConfig.configure(fieldGroup, t);
            
            if (fieldConfig.getId() != null) {
                fieldGroup.getField(fieldConfig.getId()).setReadOnly(readOnly || fieldConfig.isReadOnly());
            }
        }
        
//        if (readOnly) {
//            for (Object id : fieldLayout.getIds()) {
//                fieldGroup.getField(id).setReadOnly(readOnly || );          
//            }
//        }
//
//		} else {			
//			if (Codable.class.isAssignableFrom(getEntityClass())) {
//				if (((Codable<?>) t).getCode() != null) {
//					// we dont want the code to change because it's the entity's id
//					fieldGroup.getField("code").setReadOnly(true);
//				}
//			} else if (DBable.class.isAssignableFrom(getEntityClass())) {
//				// set the system properties to readonly
//			    for (Object id : fieldLayout.getIds()) {
//					fieldGroup.getField(id).setReadOnly(DBable.SYSTEM_PROPERTIES.contains(id));			
//				}
//			}		
	}
	
	@SuppressWarnings("unchecked")
	public T duplicate(ID id) {
		T t = storeService.load(id);
		setInstance(((Duplicatable<T>) t).duplicate());
		return t;
	}
	
	@SuppressWarnings("unchecked")
   public void duplicate() {
		setInstance(((Duplicatable<T>) fieldGroup.getItemDataSource().getBean()).duplicate());
	}
	
	public T getInstance() {
		return fieldGroup.getItemDataSource().getBean();
	}
	
	public BeanItem<T> getBeanItem() {
		return fieldGroup.getItemDataSource();
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setCustomField(String propertyId, Field<?> field) {
		customFields.put(propertyId, field);
	}
	
	public void setRelationalFieldCustomizers(RelationalFieldCustomizer[] relationalFieldCustomizers) {
		this.relationalFieldCustomizers = relationalFieldCustomizers;
	}

	public Field<?> getField(String propertyId) {
		return fieldGroup.getField(propertyId);
	}	
	
	public StorableService<T, ID> getStoreService() {
        return storeService;
    }
/*
    public void validateAndSave(AfterSave<T> afterSave) {
	    if (validate()) {
	        saveAndNotify(afterSave);
	    }
	}
		
    protected boolean validate() {
        String errorMsg = ValidationHelper.validate(fieldGroup.getItemDataSource().getBean());
        if (errorMsg != null) {
            Notification.show("Error", errorMsg, Notification.Type.ERROR_MESSAGE);
            return false;
        }

        // check their are no warnings and that we havn't displayed the warnings
        // in the last 5 seconds
        String warningMsg = ValidationHelper.validate(fieldGroup.getItemDataSource().getBean(), Warning.class);
        long currentWarningTs = System.currentTimeMillis();
        if (warningMsg != null && (currentWarningTs - lastWarningTs > 5000)) {
            Notification.show("Warning", warningMsg, Notification.Type.WARNING_MESSAGE);
            lastWarningTs = currentWarningTs;
            return false;
        }
        
        return true;
    }

    protected void saveAndNotify(AfterSave<T> afterSave) {
        try {
            T t = save();
            
            Notification.show("saved...", Notification.Type.TRAY_NOTIFICATION);
            
            afterSave.saved(t);
        } catch (FieldGroup.CommitException e) {
            LOGGER.error("Failed to save " + getEntityClass(), e);
            throw new RuntimeException(e);
        }
    }
    
	protected T save() throws FieldGroup.CommitException {				

		if (!fieldGroup.isValid()) {
			// this should never happen since validation has already been performed
			throw new FieldGroup.CommitException("Not valid");
		}
		
		// will not do much because we are not in a buffered mode
		fieldGroup.commit();
		
		T t1 = fieldGroup.getItemDataSource().getBean();
	
		T t2 = storeService.save(t1);
	
		fireEntityChange(t2);
	
		return t2;	
	}

	// ======
	
    
    public T save(T t) {
        T t2 = storeService.save(t);
    
        fireEntityChange(t2);
    
        return t2;  
    }
*/    
	
	protected SavingStep<T> getSavingSteps(SavingStep<T> finalizer) {
	        return new ValidateStep<T>(this, new CheckWarningsStep<T>(this, new SaveStep<T>(this, new NotifyStep<T>(this, finalizer) ) ) );
	}
	
	public void fireEntityChange(T t) {
		fireEvent(new EntityChangeEvent(this, PersistenceUtils.getIdValue(t), t));
	}
	
	public void cancel() {
		fieldGroup.discard();
	}
	
	public void delete() {
		final T t = fieldGroup.getItemDataSource().getBean();

		final String text = t.toString();
		ConfirmDialog.show(UI.getCurrent(), "Delete", text, "yes", "no",
				new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;

					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							storeService.delete(t);
							close();
							Notification.show(text, "deleted...", 
									Notification.Type.TRAY_NOTIFICATION);
							
							fireEntityChange(t);
						}
					}
				});
	}

	public void delete(final T t) {
		final String text = t.toString();
		ConfirmDialog.show(UI.getCurrent(), "Delete", text, "yes", "no",
				new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;

					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							storeService.delete(t);
							Notification.show(text, "deleted...",
									Notification.Type.TRAY_NOTIFICATION);
							
							fireEntityChange(t);;
						}
					}
				});
	}

	public void delete(final ID id) {
		final String text = MessageFormat.format("{0} id=''{1}''", getEntityClass().getSimpleName(), id);
		ConfirmDialog.show(UI.getCurrent(), "Delete", text, "yes", "no",
				new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;

					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							storeService.delete(id);							
							Notification.show(text, "deleted...",
									Notification.Type.TRAY_NOTIFICATION);
							
							fireEvent(new EntityChangeEvent(EditorView.this, id, null));
						}
					}
				});
	}

}
