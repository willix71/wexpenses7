package w.wexpense.rest.etag;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import w.wexpense.rest.exception.ResourceNotFoundException;
import w.wexpense.rest.exception.UnmodifiedException;
import w.wexpense.rest.utils.DButils;

//@Component
@Deprecated // using an interceptor for this now
public class SimpleVersionManager {
	
	@Autowired
	private DButils dbutils;
	
	private Map<Class<?>, String> lastUpdates = new HashMap<Class<?>, String>();
	
	public void checkAndSet(final Class<?> type, final Long id, final String lastVersion, final HttpServletResponse response) {
		if (id==null) {
			checkAndSet(getLastVersion(type), lastVersion, response);
		} else {
			Long v =dbutils.getVersion(type, id);
			if (v == null) {
				throw new ResourceNotFoundException();				
			}
			checkAndSet(String.valueOf(v), lastVersion, response);
		}
	}
	
	public void checkAndSet(final Class<?> type, final String lastVersion, final HttpServletResponse response) {
		checkAndSet(getLastVersion(type), lastVersion, response);
	}
	
	public void checkAndSet(final String currentVersion, final String lastVersion, final HttpServletResponse response) {
		if (lastVersion != null && lastVersion.equals(currentVersion)) {
			// if the versions are equal
			throw new UnmodifiedException();
		}
		setEtag(currentVersion, response);
	}
	
	public synchronized String setLastVersion(Class<?> type) {
		String c="TS:"+System.currentTimeMillis();
		lastUpdates.put(type,c);
		return c;
	}
	
	public synchronized String getLastVersion(Class<?> type) {
		String c=lastUpdates.get(type);
		if (c==null) {
			c=setLastVersion(type);
		}
		return c;
	}
	
	public void setEtag(final Class<?> type, final HttpServletResponse response) {
		setEtag(getLastVersion(type), response);
	}
	
	public void setEtag(final String etag, final HttpServletResponse response) {
		response.addHeader(HttpHeaders.ETAG, etag);
	}
}
