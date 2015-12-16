package w.wexpense.rest.etag;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import w.wexpense.rest.exception.UnmodifiedException;

public class SimpleVersionInterceptor extends HandlerInterceptorAdapter {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private final Pattern idPattern = Pattern.compile("/rest/(\\w*)/(\\w*)/$");
	private final Pattern nonIdPattern = Pattern.compile("/rest/(\\w*)/$");
	private final Pattern typePattern = Pattern.compile("/rest/(\\w*)/");
	
	private Map<String, String> lastUpdates = new HashMap<String, String>();
	
	protected synchronized String getOrSet(String type) {
		String l = lastUpdates.get(type);
		if (l==null) {
			setLastUpdate(type);
			l = lastUpdates.get(type);
		}
		return l; 
	}
	
	protected synchronized void setLastUpdate(String type) {
		lastUpdates.put(type,String.valueOf(System.currentTimeMillis()));
	}
	
	protected String getVersion(String type, String id) {
		return getOrSet(type);
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String currentVersion = null;
		
		String actualVersion = request.getHeader(HttpHeaders.IF_NONE_MATCH);
		
		LOGGER.info("preHandle {}, {}, Etag:{}", request.getMethod(),request.getRequestURI(), actualVersion);

		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// check version			
			String uri = request.getRequestURI();
			if (!uri.endsWith("/")) uri+="/";
			
			Matcher m = idPattern.matcher(uri);
			if (m.find()) {
				LOGGER.info("type: {}, id {}", m.group(1), m.group(2));
				currentVersion = getVersion(m.group(1), m.group(2));
			} else {
				m = nonIdPattern.matcher(uri);
				if (m.find()) {
					LOGGER.info("type: {}", m.group(1));
					currentVersion = getVersion(m.group(1), null);
				}
			}
		}
		
		if (currentVersion != null) {
			if (actualVersion != null && actualVersion.equals(currentVersion)) {
				throw new UnmodifiedException();
			}
			
			response.addHeader(HttpHeaders.ETAG, currentVersion);
		}		
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		LOGGER.info("postHandle {}", request.getMethod());
		
		if (!"GET".equalsIgnoreCase(request.getMethod())) {
			String uri = request.getRequestURI();
			if (!uri.endsWith("/")) uri+="/";
			
			Matcher m = typePattern.matcher(uri);
			if (m.find()) {
				String type = m.group(1);
				LOGGER.info("resetting type: {}", type);
				setLastUpdate(type);
			}
		}
	}
}
