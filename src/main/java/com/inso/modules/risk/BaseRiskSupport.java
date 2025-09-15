package com.inso.modules.risk;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.web.service.ConfigService;

public abstract class BaseRiskSupport {

    protected ConfigService mConfigService = SpringContextUtils.getBean(ConfigService.class);

    protected UserMoneyService moneyService = SpringContextUtils.getBean(UserMoneyService.class);


    public abstract boolean doVerify(UserInfo userInfo, JSONObject data);



    protected void updateCodeAmount(UserInfo userInfo, FundAccountType accountType, ICurrencyType currencyType, BigDecimal codeAmount)
    {
        moneyService.updateCodeAmount(userInfo.getId(), accountType, null, currencyType, codeAmount, null);
    }

}
