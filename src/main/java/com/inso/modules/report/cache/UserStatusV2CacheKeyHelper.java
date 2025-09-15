package com.inso.modules.report.cache;

import org.joda.time.DateTime;

public class UserStatusV2CacheKeyHelper {


    private static final String ROOT_CACHE_KEY = UserStatusV2CacheKeyHelper.class.getName();


    public static String findByUserid(int dayOfYear, long userid)
    {
        return ROOT_CACHE_KEY + "findByUserid" + dayOfYear + userid;
    }

    public static String queryByUserid(int typeHour, long userid)
    {
        return ROOT_CACHE_KEY + "queryByUserid" + typeHour + userid;
    }

    public static String queryListByUserid(DateTime dateTime, long userid)
    {
        return ROOT_CACHE_KEY + "queryListByUserid_V2" + dateTime.getDayOfYear() + userid;
    }
}
