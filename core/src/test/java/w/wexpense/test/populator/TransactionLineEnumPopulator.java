package w.wexpense.test.populator;

import java.lang.reflect.Field;
import java.sql.Types;

import w.dao.populator.entity.FieldPopulator;
import w.dao.populator.entity.fields.AbstractFieldPopulator;
import w.wexpense.model.AccountPeriod;

@FieldPopulator.AssignableFrom(type = AccountPeriod.class)
public class TransactionLineEnumPopulator<T> extends AbstractFieldPopulator<T> {

	public TransactionLineEnumPopulator(Class<?> clazz, Field field, String name) {
		super(clazz, field, name, Types.INTEGER);
	}

	@Override
	public Object getValue(T entity) {
		AccountPeriod period = (AccountPeriod) super.getValue(entity);
		return period==null?null:period.intValue();
	}

	@Override
	public void setValue(T entity, Object o) {
		AccountPeriod p = o==null?null:new AccountPeriod(((Number) o).intValue());
		super.setValue(entity, p);
	}

}
