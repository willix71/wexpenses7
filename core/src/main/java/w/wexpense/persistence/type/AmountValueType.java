package w.wexpense.persistence.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;

import w.wexpense.model.AmountValue;

public class AmountValueType implements EnhancedUserType {

	@Override
	public int[] sqlTypes() {
		return  new int[] { Types.BIGINT };
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class returnedClass() {
		return AmountValue.class;
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
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		Object object = rs.getObject(names[0]);
		if (rs.wasNull()) {
			return null;
		}

		long l = ((Number) object).longValue();
		return AmountValue.fromValue(l);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.BIGINT);
		} else {
			st.setObject(index, ((AmountValue) value).getValue(), Types.INTEGER);
		}
	}

	@Override
	public String objectToSQLString(Object value) {
		return value.toString();
	}

	@Override
	public String toXMLString(Object value) {
		return ((AmountValue) value).toString();
	}

	@Override
	public Object fromXMLString(String xmlValue) {
		return new AmountValue(xmlValue);	
	}
}
