package com.inso.modules.ad.core.cache;

public class MaterielCacheHelper {

    private static String ROOT_CACHE = MaterielCacheHelper.class.getName();

    public static String findById(long materielid)
    {
        return ROOT_CACHE + "findById" + materielid;
    }

    public static String findDetailInfoById(long materielid)
    {
        return ROOT_CACHE + "findDetailInfoById" + materielid;
    }

    public static String queryByCategory(long categoryid,long minPrice,long maxPrice)
    {
        return ROOT_CACHE + "queryByCategory" + categoryid+minPrice+maxPrice;
    }
}
