package com.inso.modules.coin.cloud_mining.cache;

public class CloudProfitConfigCacleKeyHelper {

    private static String ROOT_CACHE = CloudProfitConfigCacleKeyHelper.class.getName();

    public static String findById(long id)
    {
        return ROOT_CACHE + "findById" + id;
    }

    public static String queryAllList()
    {
        return ROOT_CACHE + "queryAllList_";
    }


}
