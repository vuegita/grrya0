package com.inso.modules.ad.core.cache;

public class AdVipLimitCacheHelper {

    private static final String ROOT_CACHE = AdVipLimitCacheHelper.class.getName();

//    public static String findById(long id)
//    {
//        return ROOT_CACHE + "findById" + id;
//    }

    public static String findByVipId(long vipid)
    {
        return ROOT_CACHE + "findByVipId2" + vipid;
    }


}
