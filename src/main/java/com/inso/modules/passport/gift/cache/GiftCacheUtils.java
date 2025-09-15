package com.inso.modules.passport.gift.cache;

public class GiftCacheUtils {

    private static final String ROOT_CACHE_KEY = GiftCacheUtils.class.getName();

    public static String findById(long id)
    {
        return ROOT_CACHE_KEY + "findById_" + id;
    }

    public static String queryAll()
    {
        return ROOT_CACHE_KEY + "queryAll";
    }
}
