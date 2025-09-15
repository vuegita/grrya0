package com.inso.modules.common.cache;

import com.inso.modules.common.model.OverviewType;

public class OverviewCacheHelper {

    private static final String ROOT_KEY = "inso_common_overview_stats_";

    public static String createItemCacheKey(OverviewType type)
    {
        return ROOT_KEY + type.getKey();
    }

    public static String createAgentItemCacheKey(OverviewType type, String username)
    {
        return ROOT_KEY + type.getKey() + username;
    }

    public static String createPlatformHisoryCacheKey(int dayOfYear)
    {
        return ROOT_KEY + "createPlatformHisoryCacheKey_" + dayOfYear;
    }

    public static String createProfileLossReportListCacheKey(boolean isProfit, int dayOfYear)
    {
        return ROOT_KEY + "createmProfileReportListCacheKey_" + isProfit + dayOfYear;
    }

    public static String createmProfileReportListByUserIdCacheKey(boolean isProfit, int dayOfYear,long userid)
    {
        return ROOT_KEY + "createmProfileReportListByUserIdCacheKey_" + isProfit + dayOfYear +userid;
    }

}
