package com.inso.modules.passport.business.cache;

import com.inso.modules.common.model.BusinessType;

public class AgentWalletCacheUtils {

    private static final String ROOT_CACHE_KEY = AgentWalletCacheUtils.class.getName();

    public static String findByOutTradeNo(String outTradeNo, BusinessType businessType)
    {
        return ROOT_CACHE_KEY + "findByOutTradeNo" + outTradeNo + businessType.getKey();
    }
}
