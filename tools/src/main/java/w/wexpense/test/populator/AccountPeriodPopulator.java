package w.wexpense.test.populator;

import java.lang.reflect.Field;
import java.sql.Types;

import w.dao.populator.entity.FieldPopulator;
import w.dao.populator.entity.fields.AbstractFieldPopulator;
import w.wexpense.model.enums.TransactionLineEnum;

@FieldPopulator.AssignableFrom(type = TransactionLineEnum.class)
public class AccountPeriodPopulator<T> extends AbstractFieldPopulator<T> {

	public AccountPeriodPopulator(Class<?> clazz, Field field, String name) {
		super(clazz, field, name, Types.INTEGER);
	}

	@Override
	public Object getValue(T entity) {
		TransactionLineEnum tl = (TransactionLineEnum) super.getValue(entity);
		return tl == null ? null : tl.getFactor();
	}

	@Override
	public void setValue(T entity, Object o) {
		if (o == null) {
			super.setValue(entity, null);
		} else {
			int i = ((Number) o).intValue();
			if (i > 0)
				super.setValue(entity, TransactionLineEnum.IN);
			else if (i < 0)
				super.setValue(entity, TransactionLineEnum.OUT);
			else
				super.setValue(entity, TransactionLineEnum.SUM);
		}
	}
}
