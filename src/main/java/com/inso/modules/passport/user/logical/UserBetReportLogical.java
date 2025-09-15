package com.inso.modules.passport.user.logical;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.inso.framework.cache.CacheManager;
import com.inso.modules.common.model.BusinessType;

/**
 * 用户投注统计
 */
public class UserBetReportLogical {

    private static final String BET_CACHE_KEY = UserBetReportLogical.class.getName() + "_user_bet_report_business_type_of_day";

    private static final String RECARGE_CACHE_KEY = UserBetReportLogical.class.getName() + "_user_bet_report_business_type_of_day";

    public static void addTodayReport(BusinessType businessType, String username, BigDecimal amount)
    {
        DateTime nowTime = new DateTime();
        int dayOfYear = nowTime.getDayOfYear();

        if(businessType == BusinessType.GAME_LOTTERY || businessType == BusinessType.GAME_ANDAR_BAHAR)
        {
            addTodayBetReport(dayOfYear, username, amount);
        }
        else if(businessType == BusinessType.USER_RECHARGE)
        {
            addTodayRechargeReport(dayOfYear, username, amount);
        }
    }

    private static void addTodayRechargeReport(int dayOfYear, String username, BigDecimal amount)
    {
        String cachekey = RECARGE_CACHE_KEY + username + dayOfYear;
        BigDecimal historyAmount = CacheManager.getInstance().getObject(cachekey, BigDecimal.class);
        if(historyAmount == null)
        {
            historyAmount = amount;
        }
        else
        {
            historyAmount = historyAmount.add(amount);
        }
        CacheManager.getInstance().setString(cachekey, historyAmount.toString(), CacheManager.EXPIRES_DAY);
    }

    private static void addTodayBetReport(int dayOfYear, String username, BigDecimal amount)
    {
        String cachekey = BET_CACHE_KEY + username + dayOfYear;
        BigDecimal historyAmount = CacheManager.getInstance().getObject(cachekey, BigDecimal.class);
        if(historyAmount == null)
        {
            historyAmount = amount;
        }
        else
        {
            historyAmount = historyAmount.add(amount);
        }
        CacheManager.getInstance().setString(cachekey, historyAmount.toString(), CacheManager.EXPIRES_DAY);
    }

    public static BigDecimal getTotalBetAmount(String username)
    {
        DateTime nowTime = new DateTime();
        int dayOfYear = nowTime.getDayOfYear();

        String cachekey = BET_CACHE_KEY + username + dayOfYear;
        BigDecimal historyAmount = CacheManager.getInstance().getObject(cachekey, BigDecimal.class);
        if(historyAmount == null)
        {
            return BigDecimal.ZERO;
        }
        return historyAmount;
    }


}
