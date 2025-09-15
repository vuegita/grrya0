package com.inso.modules.game.rocket.cache;

import com.inso.modules.game.rocket.model.RocketType;
import org.joda.time.DateTime;

public class RocketCacheHelper {

    private static final String ROOT_CACHE_KEY = RocketCacheHelper.class.getName();

    public static String findInfo(String issue)
    {
        return ROOT_CACHE_KEY + "_findInfo_" + issue;
    }

    public static String queryLatestPage_100(long userid, RocketType rgType)
    {
        return ROOT_CACHE_KEY + "queryScrollPageByUser_latest_100" + userid + rgType.getKey();
    }

    public static String createIssueIndex(RocketType rgType, DateTime dateTime)
    {
        return ROOT_CACHE_KEY + "createIssueIndex" + rgType.getKey() + dateTime.getDayOfYear();
    }

    public static String findByOrderInfoByIssue(String issue, long userid)
    {
        return ROOT_CACHE_KEY + "findByOrderInfoByIssue" + issue + userid;
    }

    public static String cashout(String issue, String username)
    {
        return ROOT_CACHE_KEY + "cashout" + issue + username;
    }
}
