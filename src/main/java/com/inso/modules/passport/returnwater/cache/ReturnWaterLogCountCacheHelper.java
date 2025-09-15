package com.inso.modules.passport.returnwater.cache;

public class ReturnWaterLogCountCacheHelper {

    private static final String ROOT_CACHE_KEY = ReturnWaterLogCountCacheHelper.class.getName();

    public static String findByUser(long userid)
    {
        return ROOT_CACHE_KEY + "findByUser" + userid;
    }
}
