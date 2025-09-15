package com.inso.modules.ad.core.cache;

public class AdWithdrawalLimitCacheHelper {

    private static final String ROOT_CACHE = AdWithdrawalLimitCacheHelper.class.getName();

    public static String findByUserId(long id)
    {
        return ROOT_CACHE + "findByUserId" + id;
    }

}
