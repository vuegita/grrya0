package com.inso.modules.passport.money.cache;

import com.inso.modules.passport.MyConstants;

public class MoneyOrderCacheUtils {

    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_PASSPORT_MODULE_NAME + "_money_order_";

    public static String queryLatestPage_100(long userid)
    {
        return ROOT_CACHE_KEY + "queryScrollPageByUser_latest_100" + userid;
    }

    public static String findHistoryByDateTime(int period, long userid)
    {
        return ROOT_CACHE_KEY + "findHistoryByDateTime" + period + userid;
    }
}
