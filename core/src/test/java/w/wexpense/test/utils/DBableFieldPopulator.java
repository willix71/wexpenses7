package w.wexpense.test.utils;

import java.lang.reflect.Field;
import java.sql.Types;

import w.dao.populator.entity.FieldPopulator;
import w.dao.populator.entity.fields.AbstractFieldPopulator;
import w.wexpense.model.DBable;

@FieldPopulator.AssignableFrom(type=DBable.class)
public class DBableFieldPopulator<T> extends AbstractFieldPopulator<T> {

   public DBableFieldPopulator(Class<?> clazz, Field field, String name) {
      super(clazz, field, name, Types.NUMERIC);
   }
   
   @Override
   public String getName() {
      return super.getName() + "_id";
   }

   @Override
   public Object getValue(T entity) {
      DBable<?> idable = (DBable<?>) super.getValue(entity);
      if (idable == null) return null;
      return idable.getId();
   }
}
