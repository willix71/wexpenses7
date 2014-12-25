package w.wexpense.vaadin7.event;

import java.io.Serializable;

import com.vaadin.ui.Component;

public class SelectionChangeEvent<T> extends Component.Event {

    private static final long serialVersionUID = -3060677120276997236L;

    private Serializable id;
    private T bean;

    public SelectionChangeEvent(Component source) {
        super(source);
    }

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public T getBean() {
        return bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }
}


