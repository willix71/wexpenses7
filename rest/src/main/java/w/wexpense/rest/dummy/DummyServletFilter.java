package w.wexpense.rest.dummy;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(urlPatterns = { "/*" })
public class DummyServletFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(DummyServletFilter.class);

	private FilterConfig config = null;

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		config.getServletContext().log("Initializing Dummy Filter");
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		LOGGER.info("Called\n");
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		//
		// Check to see if user's session attribute contains an attribute
		// named AUTHENTICATED. If the attribute is not exists redirect
		// user to the login page.
		//
		// if (!request.getRequestURI().endsWith("login.jsp")
		// && request.getSession().getAttribute("AUTHENTICATED") == null) {
		// response.sendRedirect(request.getContextPath() + "/login.jsp");
		// }
		response.addHeader("Dummy", "OK");

		chain.doFilter(req, res);
	}

	public void destroy() {
		config.getServletContext().log("Destroying SessionCheckerFilter");
	}

}
