package w.wexpense.rest.etag;

import w.wexpense.rest.utils.DButils;

public class DBableVersionInterceptor extends SimpleVersionInterceptor {

	private DButils dbutils;
	
	public DBableVersionInterceptor(DButils dbutils) {
		this.dbutils = dbutils;
	}
	
	@Override
	protected String getVersion(String type, String id) {
		if (id == null) {
			return getOrSet(type);
		} else {
			String tableName = type.substring(0, 1).toUpperCase() + type.substring(1);
			return String.valueOf(dbutils.getVersion(tableName, Long.valueOf(id)));
		}
		
	}
}
