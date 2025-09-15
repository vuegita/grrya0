package com.inso.modules.common.helper;

import com.inso.framework.cache.CacheManager;

public class RequestTokenHelper {

    /*** 2s内只能一个 ***/
    private static int DEFAULT_GAME_EXPIRES = 2;

    private static String ROOT_CACHE_KEY = RequestTokenHelper.class.getName();

    private static String DEFAULT_CACHE_VALUE = "1";

    public static boolean verifyGame(String username)
    {
        String cachekey = ROOT_CACHE_KEY + "_game_request_" + username;
        boolean exist = CacheManager.getInstance().exists(cachekey);
        if(exist)
        {
            return false;
        }

        synchronized (username)
        {
            exist = CacheManager.getInstance().exists(cachekey);
            if(exist)
            {
                return false;
            }

            CacheManager.getInstance().setString(cachekey, DEFAULT_CACHE_VALUE, DEFAULT_GAME_EXPIRES);
        }

        return true;
    }

    public static boolean verifCashout(String username, String issue)
    {
        String cachekey = ROOT_CACHE_KEY + "_cashout_request_" + username + issue;
        boolean exist = CacheManager.getInstance().exists(cachekey);
        if(exist)
        {
            return false;
        }

        synchronized (username)
        {
            exist = CacheManager.getInstance().exists(cachekey);
            if(exist)
            {
                return false;
            }

            CacheManager.getInstance().setString(cachekey, DEFAULT_CACHE_VALUE, 5);
        }
        return true;
    }

    public static boolean verifyStaking(String username)
    {
        String cachekey = ROOT_CACHE_KEY + "_staking_request_" + username;
        boolean exist = CacheManager.getInstance().exists(cachekey);
        if(exist)
        {
            return false;
        }

        synchronized (username)
        {
            exist = CacheManager.getInstance().exists(cachekey);
            if(exist)
            {
                return false;
            }

            CacheManager.getInstance().setString(cachekey, DEFAULT_CACHE_VALUE, 180);
        }

        return true;
    }


}
