package com.inso.modules.web.cache;

public class KefuCacheHelper {

    private static String ROOT_CACHE_KEY = KefuCacheHelper.class.getName();

    public static String getOnlineKefuList()
    {
        return ROOT_CACHE_KEY + "getOnlineKefuList";
    }

    public static String getOnlinestaffKefuList(long staffid)
    {
        return ROOT_CACHE_KEY + "getOnlinestaffKefuList"+staffid;
    }

    public static String getFeedBackListByUserid(long userid,String status)
    {
        return ROOT_CACHE_KEY + "getFeedBackListByUserid"+userid+status;
    }

}
