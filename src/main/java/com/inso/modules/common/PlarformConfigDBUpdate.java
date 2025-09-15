package com.inso.modules.common;

import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlarformConfigDBUpdate {

    @Autowired
    private ConfigService mConfigService;


    public void updateDB()
    {
        PlarformConfig2[] values = PlarformConfig2.values();

        for (PlarformConfig2 type : values)
        {
            ConfigKey configKey = mConfigService.findByKey(true, type.getKey());
            if(configKey == null)
            {
                mConfigService.addConfig(type.getKey(), type.getValue());
            }
        }

        mConfigService.findByList(true, "admin_platform_config");
    }

}
