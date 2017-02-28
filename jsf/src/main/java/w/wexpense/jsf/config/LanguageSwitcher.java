package w.wexpense.jsf.config;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "switcher")
@SessionScoped
public class LanguageSwitcher {
    private Locale locale;
    
    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    }
    
    public Locale getLocale() {
        return locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }
    
    public String switchLocale(String language) {
        locale = new Locale(language);

        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);        
        
        return FacesContext.getCurrentInstance().getViewRoot().getViewId() + "?faces-redirect=true";
    }
   
}