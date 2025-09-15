package com.inso.modules.passport.user.logical;

import com.inso.framework.cache.CacheManager;

public class UserRecentBetStatus {

    private static int DEFAULT_EXPIRES = 60 * 6;

    private static String ROOT_CACHE = UserRecentBetStatus.class.getName() + "_Bet";
    private static String DEFAULT_VALUE = "1";

    public static void save(String username)
    {
        String userCacheKey = ROOT_CACHE + username ;
        CacheManager.getInstance().setString(userCacheKey, DEFAULT_VALUE, DEFAULT_EXPIRES);
    }

    public static boolean exists(String username)
    {
        String userCacheKey = ROOT_CACHE + username ;
        return CacheManager.getInstance().exists(userCacheKey);
    }

}
