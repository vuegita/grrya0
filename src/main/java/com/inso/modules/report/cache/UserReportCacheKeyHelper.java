package com.inso.modules.report.cache;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.MemberReport;
import org.joda.time.DateTime;

import java.util.List;

public class UserReportCacheKeyHelper {


    private static final String ROOT_CACHE_KEY = UserReportCacheKeyHelper.class.getName();


    public static String queryHistoryReportByUser(DateTime fromTime, DateTime toTime, long userid, FundAccountType accountType, ICurrencyType currencyType)
    {
        return ROOT_CACHE_KEY + fromTime.getDayOfYear() + toTime.getDayOfYear() + userid + accountType.getKey() + currencyType.getKey();
    }

    public static String queryByUser(long userid, FundAccountType accountType, ICurrencyType currencyType)
    {
        return ROOT_CACHE_KEY + "queryByUser" + userid + accountType.getKey() + currencyType.getKey();
    }

    public static String queryAgentDataListByWebApi(DateTime nowTime, long userid, UserInfo.UserType userType)
    {
        return ROOT_CACHE_KEY + "queryAgentDataListByWebApi" + nowTime.getDayOfYear() + userid + userType.getKey();
    }

}
