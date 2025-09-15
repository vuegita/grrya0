package com.inso.modules.ad.mall.cache;

public class DispatchConfigCacheHelper {

    private static final String ROOT_CACHE = DispatchConfigCacheHelper.class.getName();


    public static String findByKey(String id)
    {
        return ROOT_CACHE + "findByKey" + id;
    }

    public static String queryAll()
    {
        return ROOT_CACHE + "queryAll";
    }

}
