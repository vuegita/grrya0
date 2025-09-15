package com.inso.modules.passport.business.cache;

public class PromotionOrderCacheUtils {

    private static final String ROOT_CACHE_KEY = PromotionOrderCacheUtils.class.getName();

    public static String queryLatestPage_100(long userid)
    {
        return ROOT_CACHE_KEY + "queryLatestPage_100" + userid;
    }

    public static String findByNo(String orderno)
    {
        return ROOT_CACHE_KEY + "findByNo" + orderno;
    }

}
