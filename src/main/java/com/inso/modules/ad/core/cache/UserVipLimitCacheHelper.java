package com.inso.modules.ad.core.cache;

import com.inso.modules.web.model.VIPType;

public class UserVipLimitCacheHelper {

    private static String ROOT_CACHE_KEY = UserVipLimitCacheHelper.class.getName();

    public static String queryAllEnable(VIPType vipType)
    {
        return ROOT_CACHE_KEY + "queryAllEnable" + vipType.getKey();
    }
}
