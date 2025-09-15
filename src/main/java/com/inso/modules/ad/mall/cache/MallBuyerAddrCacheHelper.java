package com.inso.modules.ad.mall.cache;

public class MallBuyerAddrCacheHelper {

    private static final String ROOT_CACHE = MallBuyerAddrCacheHelper.class.getName();


    public static String findUserid(long merchantid)
    {
        return ROOT_CACHE + "findUserid" + merchantid;
    }


}
