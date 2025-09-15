package com.inso.modules.passport.user.logical;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.config.CodeAmountConfig;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;

@Component
public class CodeAmountDBUpdate {

    @Autowired
    private ConfigService mConfigService;


    public void updateDB()
    {
        CodeAmountConfig[] values = CodeAmountConfig.values();

        for (CodeAmountConfig type : values)
        {
            addConfig(type, MemberSubType.SIMPLE);
            addConfig(type, MemberSubType.PROMOTION);
        }

    }

    private void addConfig(CodeAmountConfig type, MemberSubType subType)
    {
        String key = type.getKey(subType);

//        System.out.println("key = " + key);

        ConfigKey configKey = mConfigService.findByKey(true, key);
        if(configKey == null)
        {
            mConfigService.addConfig(key, type.getMultiCount() + StringUtils.getEmpty());
        }
    }
}
