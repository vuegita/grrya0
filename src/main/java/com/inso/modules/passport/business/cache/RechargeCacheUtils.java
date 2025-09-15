package com.inso.modules.passport.business.cache;

import com.inso.modules.passport.MyConstants;

public class RechargeCacheUtils {

    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_PASSPORT_MODULE_NAME + "_recharge_";

    public static String queryLatestPage_100(long userid, boolean isWaiting)
    {
        return ROOT_CACHE_KEY + "queryScrollPageByUser_latest_100" + userid + isWaiting;
    }
}
