package com.inso.modules.coin.core;

import com.inso.framework.cache.CacheManager;

public class ModifyAdddressHelper {

    private static String ROOT_CACHE = ModifyAdddressHelper.class.getName();

    private static String DEF_VALUE = "1";

    public static void saveStatus(String address)
    {
        String cachekey = ROOT_CACHE + address;
        CacheManager.getInstance().setString(cachekey, DEF_VALUE, CacheManager.EXPIRES_DAY);
    }

    public static boolean exist(String address)
    {
        String cachekey = ROOT_CACHE + address;
        return CacheManager.getInstance().exists(cachekey);
    }

}
