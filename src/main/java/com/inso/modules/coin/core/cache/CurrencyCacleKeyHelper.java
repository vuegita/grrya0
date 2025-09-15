package com.inso.modules.coin.core.cache;

public class CurrencyCacleKeyHelper {

    private static String ROOT_CACHE = CurrencyCacleKeyHelper.class.getName();


    public static String findByKey(String key)
    {
        return ROOT_CACHE + "findByKey" + key;
    }

    public static String queryLatestPage_100()
    {
        return ROOT_CACHE + "queryLatestPage_100";
    }


}
