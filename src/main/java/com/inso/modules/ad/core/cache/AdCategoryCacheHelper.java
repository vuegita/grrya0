package com.inso.modules.ad.core.cache;

public class AdCategoryCacheHelper {

    private static final String ROOT_CACHE = AdCategoryCacheHelper.class.getName();

    public static String findById(long id)
    {
        return ROOT_CACHE + "_findById_" + id;
    }

    public static String findByKey(String key)
    {
        return ROOT_CACHE + "findByKey" + key;
    }



    public static String queryAllEnable()
    {
        return ROOT_CACHE + "queryAllEnable";
    }
}
