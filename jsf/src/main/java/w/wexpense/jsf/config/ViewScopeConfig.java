package w.wexpense.jsf.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;

import com.github.javaplugs.jsf.ViewScope;

public class ViewScopeConfig extends CustomScopeConfigurer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewScopeConfig.class);
	
    public ViewScopeConfig() {
    	LOGGER.info("Init ViewScope");
        Map<String, Object> map = new HashMap<>();
        map.put("view", new ViewScope());
        super.setScopes(map);
    }

}
