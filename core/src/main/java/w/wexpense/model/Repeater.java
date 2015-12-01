package w.wexpense.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class Repeater extends DBable<Repeater> {

   private static final long serialVersionUID = 2482940442245899869L;
  
   @NotNull
   private String name;
   
   private Integer times;
   
   @NotNull
   private String cron;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Integer getTimes() {
      return times;
   }

   public void setTimes(Integer times) {
      this.times = times;
   }

   public String getCron() {
      return cron;
   }

   public void setCron(String cron) {
      this.cron = cron;
   }
      
   @Override
   public String toString() {        
      return name + "{" + cron+ "{";
   }
   
}
