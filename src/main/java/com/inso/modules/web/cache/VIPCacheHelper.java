package com.inso.modules.web.cache;

import com.inso.modules.web.model.VIPType;

public class VIPCacheHelper {

    private static String ROOT_CACHE_KEY = VIPCacheHelper.class.getName();


    public static String queryAllEnable(VIPType vipType)
    {
        return ROOT_CACHE_KEY + "queryAllEnable" + vipType.getKey();
    }

    public static String findById(long id)
    {
        return ROOT_CACHE_KEY + "findById" + id;
    }

    public static String findFree(VIPType vipType)
    {
        return ROOT_CACHE_KEY + "findFree" + vipType.getKey();
    }


}
