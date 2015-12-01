package w.wexpense.vaadin7;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.DiscoveryNavigator;
import w.wexpense.model.Payment;
import w.wexpense.service.StorableService;
import w.wexpense.utils.PaymentDtaUtils;
import w.wexpense.vaadin7.converter.WexConverterFactory;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import figlet.FigletUtil;
import figlet.FigletUtil.Figlet;

@Component
@Scope("prototype")
public class WexUI extends UI {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

   @Autowired
   private transient ApplicationContext applicationContext;

	@Override
	protected void init(VaadinRequest request) {
		LOGGER.debug("\n{}", FigletUtil.getMessage(getClass().getSimpleName() + " init", Figlet.CHUNKY));
		
		setSizeFull();

		VaadinSession.getCurrent().setConverterFactory(new WexConverterFactory());
		
      DiscoveryNavigator navigator = new DiscoveryNavigator(this, this);
      navigator.navigateTo(UI.getCurrent().getPage().getUriFragment());

      // request handler used to download a payment DTA
		VaadinSession.getCurrent().addRequestHandler(new RequestHandler() {
			@Override
			public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) throws IOException {
				if ("/download".equals(request.getPathInfo())) {
					StorableService<Payment, Long> paymentService = getBean(StorableService.class, "paymentService");
					Long id = Long.valueOf(request.getParameter("paymentId"));
					Payment p = paymentService.load(id);

					response.setHeader("Content-Disposition", "attachment; filename=" + p.getFilename());
					response.setContentType("text/plain");
					response.getWriter().append(PaymentDtaUtils.getDtaLines(p.getDtaLines(), true));
					return true; // We wrote a response
				} 

				return false; // No response was written
			}
		});
      
      Notification.show("Welcome");
	}
	
	public <T> T getBean(Class<T> type) {
		return applicationContext.getBean(type);
	}
	public <T> T getBean(Class<T> type, String name) {
		if (name==null || name.length()==0) {
			return applicationContext.getBean(type);
		} else {
			return applicationContext.getBean(name, type);
		}
	}
}
