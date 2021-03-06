package w.wexpense.persistence.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;

import w.wexpense.model.AccountPeriod;

public class AccountPeriodType implements EnhancedUserType {

	@Override
	public int[] sqlTypes() {
		return  new int[] { Types.INTEGER };
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class returnedClass() {
		return AccountPeriod.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return x == y;
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x == null ? 0 : x.hashCode();
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return ( Serializable ) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		
		Object object = rs.getObject(names[0]);
		if (rs.wasNull()) {
			return null;
		}

		return AccountPeriod.valueOf(((Number) object).intValue());
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) 
			throws HibernateException, SQLException {
		
		if (value == null) {
			st.setNull(index, Types.INTEGER);
		} else {
			st.setObject(index, ((AccountPeriod) value).intValue(), Types.INTEGER);
		}
	}

	@Override
	public String objectToSQLString(Object value) {
		if (value==null) return "";
		if (value instanceof AccountPeriod) {
			return String.valueOf(((AccountPeriod) value).intValue());
		}
		return value.toString();
	}

	@Override
	public String toXMLString(Object value) {
		return ((AccountPeriod) value).toString();
	}

	@Override
	public Object fromXMLString(String xmlValue) {
		return AccountPeriod.valueOf(xmlValue);	
	}
}
