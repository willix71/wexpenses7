package w.wexpense.rest.etag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import w.wexpense.rest.exception.ResourceNotFoundException;
import w.wexpense.rest.utils.DButils;

public class DBableVersionInterceptor extends SimpleVersionInterceptor {

	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private DButils dbutils;
	
	public DBableVersionInterceptor(DButils dbutils) {
		this.dbutils = dbutils;
	}
	
	@Override
	protected String getVersion(String type, String id) {
		if (id == null) {
			// fetching list so fall back to default behavior
			return getOrSet(type);
		} else {
			// capitalize first letter because JPL is case sensitive			
			String tableName = StringUtils.capitalize(type);
			Long v = dbutils.getVersion(tableName, Long.valueOf(id));
			if (v==null) {
				throw new ResourceNotFoundException();
			}
			return String.valueOf(v);
		}
		
	}
}
