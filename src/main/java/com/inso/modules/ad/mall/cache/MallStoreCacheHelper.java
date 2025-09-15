package com.inso.modules.ad.mall.cache;

public class MallStoreCacheHelper {

    private static final String ROOT_CACHE = MallStoreCacheHelper.class.getName();


    public static String findUserid(long merchantid)
    {
        return ROOT_CACHE + "findMerchantid" + merchantid;
    }


}
