package com.inso.modules.report.cache;

public class UserStatusCacheKeyHelper {


    private static final String ROOT_CACHE_KEY = UserStatusCacheKeyHelper.class.getName();


    public static String querySubStatsInfoByAgent(int dayOfYear, long userid, int periodOfDay)
    {
        return ROOT_CACHE_KEY + "querySubStatsInfoByAgent" + dayOfYear + userid + periodOfDay;
    }

    public static String queryListByAgent(long userid)
    {
        return ROOT_CACHE_KEY + "queryListByAgent" + userid;
    }


}
