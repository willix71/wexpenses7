package w.wexpense.test.utils;

import java.lang.reflect.Field;
import java.sql.Types;

import w.dao.populator.entity.FieldPopulator;
import w.dao.populator.entity.fields.AbstractFieldPopulator;
import w.wexpense.model.Codable;

@FieldPopulator.AssignableFrom(type=Codable.class)
public class CodableFieldPopulator<T> extends AbstractFieldPopulator<T> {

   public CodableFieldPopulator(Class<?> clazz, Field field, String name) {
      super(clazz, field, name, Types.VARCHAR);
   }
   
   @Override
   public String getName() {
      return super.getName() + "_code";
   }
   
   @Override
   public Object getValue(T entity) {
      Codable<?> idable = (Codable<?>) super.getValue(entity);
      if (idable == null) return null;
      return idable.getCode();
   }
}
