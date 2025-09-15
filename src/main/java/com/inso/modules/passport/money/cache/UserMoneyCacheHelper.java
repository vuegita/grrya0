package com.inso.modules.passport.money.cache;

import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.MyConstants;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;

import java.util.List;

public class UserMoneyCacheHelper {

    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_PASSPORT_MODULE_NAME + UserMoneyCacheHelper.class.getName();

    public static String createGetUserBalanceCacheKey(long userid, FundAccountType accountType, ICurrencyType currencyType)
    {
        return ROOT_CACHE_KEY + "_createGetUserBalanceCacheKey_" + userid + accountType.getKey() + currencyType.getKey();
    }



    public static String queryAllMoneyByParentUserid(long userid, FundAccountType accountType, UserInfo.UserType userType)
    {
        return ROOT_CACHE_KEY + "queryAllMoneyByParentUserid" + userid + accountType.getKey() + userType.getKey();
    }

    public static String queryUserListByStaffid(DateTime dateTime, long userid)
    {
        return ROOT_CACHE_KEY + "queryUserListByStaffid" + dateTime.getDayOfYear() + userid;
    }


    public static String createQueryUserBalanceListCacheKey(long userid, FundAccountType accountType)
    {
        return ROOT_CACHE_KEY + "createQueryUserBalanceListCacheKey" + userid + accountType.getKey();
    }


    public static String createUserWithdrawTimesOfDayLimit(String username, int dayOfYear)
    {
        return ROOT_CACHE_KEY + "createUserWithdrawTimesOfDayLimit" + username + dayOfYear;
    }

    public static String createUserWithdrawTotalAmountOfDay(String username, int dayOfYear)
    {
        return ROOT_CACHE_KEY + "createUserWithdrawTotalAmountOfDay" + username + dayOfYear;
    }

    public static String createUserMoneyOrderList(long userid,int limit)
    {
        return ROOT_CACHE_KEY + "createUserMoneyOrderList" + userid + limit;
    }

    public static String queryTotalNumByUserid(long userid)
    {
        return ROOT_CACHE_KEY + "queryTotalNumByUserid" + userid ;
    }

    public static String queryScrollPageByUser(long userid,int limit)
    {
        return ROOT_CACHE_KEY + "queryScrollPageByUser" + userid + limit;
    }

    public static String queryTotalNumByUser(long userid)
    {
        return ROOT_CACHE_KEY + "queryTotalNumByUser" + userid;
    }

    public static String queryScrollPageByUser(long userid, int limit, CryptoCurrency currency, CloudProductType productType , CloudOrderInfo.OrderType orderType)
    {
        return ROOT_CACHE_KEY + "queryScrollPageByUser" + userid + limit +currency + productType + orderType;
    }

    public static String queryTotalNumByUser(long userid,  CryptoCurrency currency, CloudProductType productType , CloudOrderInfo.OrderType orderType)
    {
        return ROOT_CACHE_KEY + "queryTotalNumByUser" + userid  + currency + productType + orderType;
    }

    public static String countActive(int dayOfYear, int fromDays)
    {
        return ROOT_CACHE_KEY + "countActive" + dayOfYear + fromDays;
    }


}
