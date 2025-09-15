package com.inso.modules.web.cache;

public class BannerCacheHelper {

    private static String ROOT_CACHE_KEY = BannerCacheHelper.class.getName();


    public static String getBannerListBystatus(String status)
    {
        return ROOT_CACHE_KEY + "getBannerListBystatus" + status;
    }

}
