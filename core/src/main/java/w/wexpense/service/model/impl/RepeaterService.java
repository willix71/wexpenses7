package w.wexpense.service.model.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import w.wexpense.model.Repeater;
import w.wexpense.persistence.dao.IRepeaterJpaDao;
import w.wexpense.service.JpaRepoDaoService;
import w.wexpense.service.instanciator.NameInitializor;
import w.wexpense.service.model.IRepeaterService;
;

@Service
public class RepeaterService extends JpaRepoDaoService<Repeater, Long> implements IRepeaterService {

	@Autowired
	public RepeaterService(IRepeaterJpaDao dao) {
	   super(Repeater.class, dao, new NameInitializor<Repeater>(Repeater.class));
   }

   @Override
   @Scheduled(cron="* * 0 * * *")
   public void execute() {
      LOGGER.debug("checking repeaters...");      
   }

}
