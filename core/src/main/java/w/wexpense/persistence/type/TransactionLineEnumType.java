package w.wexpense.persistence.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;

import w.wexpense.model.enums.TransactionLineEnum;

/**
 * Class used to store a ValueAmount object in a database.
 * 
 * @author gekeysew
 *
 */
public class TransactionLineEnumType implements EnhancedUserType {

	@Override
	public int[] sqlTypes() {
		return  new int[] { Types.INTEGER };
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class returnedClass() {
		return TransactionLineEnum.class;
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

		int v = ((Number) object).intValue();
		return fromInt(v);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.INTEGER);
		} else {
			int ordinal = ((TransactionLineEnum) value).getFactor();
			st.setObject(index, Integer.valueOf(ordinal), Types.INTEGER);
		}
	}

	@Override
	public String objectToSQLString(Object value) {
		return value.toString();
	}

	@Override
	public String toXMLString(Object value) {
		return Integer.toString(((TransactionLineEnum) value).getFactor());
	}

	@Override
	public Object fromXMLString(String xmlValue) {
		int v = Integer.parseInt( xmlValue );
		return fromInt(v);		
	}
	
	private TransactionLineEnum fromInt(int i) {
		if (i > 0)
			return TransactionLineEnum.IN;
		if (i < 0)
			return TransactionLineEnum.OUT;
		return TransactionLineEnum.SUM;
	}
}
