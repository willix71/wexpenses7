package w.wexpense.vaadin7.component;

import java.io.Serializable;

import w.wexpense.model.DBable;
import w.wexpense.vaadin7.UIHelper;
import w.wexpense.vaadin7.event.EntityChangeEvent;
import w.wexpense.vaadin7.view.EditorView;
import w.wexpense.vaadin7.view.GenericView;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

public class WexComboBox<T, ID extends Serializable> extends CustomField<T> implements Button.ClickListener {

    private static final long serialVersionUID = -5068539384518692094L;

    private Class<T> entityClass;

    private HorizontalLayout layout;
    private ComboBox comboBox;
    private Button add;
    private Label label;
    private String text;

    public WexComboBox(Class<T> entityClass, Container comboContainer) {
        this.entityClass = entityClass;

        comboBox = new ComboBox(null, comboContainer);
        comboBox.setSizeFull();
        comboBox.setImmediate(true);
        comboBox.setConverter(new SingleSelectConverter<T>(comboBox));
        comboBox.setItemCaptionMode(NativeSelect.ItemCaptionMode.ITEM);
        comboBox.setFilteringMode(FilteringMode.CONTAINS);

        label = new Label("");
        label.setVisible(false);
        
        layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.addComponent(label);
        layout.setExpandRatio(label, 100);
        layout.addComponent(comboBox);
        layout.setExpandRatio(comboBox, 100);

        if (DBable.class.isAssignableFrom(entityClass)) {
            add = new Button();
            add.setStyleName("link");
            add.setIcon(new ThemeResource("../runo/icons/16/document-add.png"));
            add.addClickListener((Button.ClickListener) this);
            layout.addComponent(add);

            comboBox.setNewItemsAllowed(true);
            comboBox.setNewItemHandler(new NewItemHandler() {
                private static final long serialVersionUID = 1L;

                public void addNewItem(String newItemCaption) {
                    text = newItemCaption;
                    comboBox.setValue(null);
                }
            });

        }
    }

    @Override
    protected Component initContent() {
        return layout;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        GenericView<T> view = addNew();

        UIHelper.displayModalWindow(view);
    }

    protected GenericView<T> addNew() {
        final EditorView<T, ID> editor = (EditorView<T, ID>) UIHelper.getEditorView(entityClass);

        // disable edit menu???

        ID id = (ID) comboBox.getValue();
        if (id == null) {
            editor.newInstance(text);
        } else {
            // reload the instance so that if modified but canceled,
            // the changes are not reflected in the current instance
            // (also prevents lazy loading exceptions)
            editor.loadInstance(id);
        }

        editor.addListener(new Component.Listener() {
            private static final long serialVersionUID = 8121179082149508635L;

            @Override
            public void componentEvent(Event event) {
                if (event instanceof EntityChangeEvent && event.getComponent() == editor) {
                    // refresh the source
                    ((JPAContainer<?>) comboBox.getContainerDataSource()).refresh();

                    // set the new value
                    comboBox.setValue(((EntityChangeEvent) event).getId());
                }
            }
        });

        return editor;
    }

    @Override
    public Class<T> getType() {
        return entityClass;
    }

    @Override
    public void commit() throws SourceException, InvalidValueException {
        comboBox.commit();
    }

    @Override
    public void discard() throws SourceException {
        comboBox.discard();
    }

    @Override
    public T getValue() {
        @SuppressWarnings("unchecked")
        T t = (T) comboBox.getConvertedValue();
        return t;
    }

    @Override
    public void setValue(T newValue) throws ReadOnlyException, ConversionException {
        Object converted = comboBox.getConverter().convertToPresentation(newValue, null);
        comboBox.setValue(converted);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        comboBox.setVisible(!readOnly);
        add.setVisible(!readOnly);
        label.setVisible(readOnly);
        super.setReadOnly(readOnly);
    }

    @Override
    public void markAsDirty() {
        if (comboBox != null) {
            comboBox.markAsDirty();
        }
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        Object value = newDataSource==null?null:newDataSource.getValue();
        label.setValue(value==null?"":value.toString());
        comboBox.setPropertyDataSource(newDataSource);
    }

    @Override
    public Property<T> getPropertyDataSource() {
        return comboBox.getPropertyDataSource();
    }

    @Override
    public void addValidator(Validator validator) {
        comboBox.addValidator(validator);
    }

    @Override
    public void removeValidator(Validator validator) {
        comboBox.removeValidator(validator);
    }

    @Override
    public void addValueChangeListener(ValueChangeListener listener) {
        comboBox.addValueChangeListener(listener);
    }

    @Override
    public void removeValueChangeListener(ValueChangeListener listener) {
        comboBox.removeValueChangeListener(listener);
    }
}