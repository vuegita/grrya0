package com.inso.modules.passport.returnwater.cache;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;

public class ReturnWaterLogAmountCacheHelper {

    private static final String ROOT_CACHE_KEY = ReturnWaterLogAmountCacheHelper.class.getName();

    public static String findById(int level, long userid, long childid, FundAccountType accountType, ICurrencyType currencyType)
    {
        return ROOT_CACHE_KEY + "findById" + level + userid + childid + accountType.getKey() + currencyType.getKey();
    }

    public static String queryListByUser(long userid)
    {
        return ROOT_CACHE_KEY + "queryListByUser" + userid;
    }
}
