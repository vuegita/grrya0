package com.inso.modules.game.rocket.helper;

import com.inso.framework.cache.CacheManager;
import com.inso.modules.game.rocket.cache.RocketCacheHelper;

public class RocketCashoutHelper {

    private static final String DEF_VALUE = "1";
    private static final int DEF_EXPIRES = 300;

    public static void saveCashoutStatus(String issue, String username)
    {
        String cachekey = RocketCacheHelper.cashout(issue, username);
        CacheManager.getInstance().setString(cachekey, DEF_VALUE, DEF_EXPIRES);
    }

    public static boolean hasCashout(String issue, String username)
    {
        String cachekey = RocketCacheHelper.cashout(issue, username);
        return CacheManager.getInstance().exists(cachekey);
    }

}
